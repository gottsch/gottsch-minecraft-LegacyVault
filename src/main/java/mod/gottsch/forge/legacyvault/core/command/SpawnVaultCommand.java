/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Legacy Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Legacy Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Legacy Vault.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.legacyvault.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.gottsch.forge.gottschcore.command.CommandResponseFormatter;
import mod.gottsch.forge.gottschcore.spatial.DimensionCoords;
import mod.gottsch.forge.gottschcore.spatial.Heading;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.CommunityVaultBlock;
import mod.gottsch.forge.legacyvault.core.block.ILegacyVaultBlock;
import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import mod.gottsch.forge.legacyvault.core.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * /legacyvault spawn <pos> [targets] [type] [direction]
 *
 * @author Mark Gottschling on Jun 5, 2021
 */
public class SpawnVaultCommand {

    private static final List<String> VAULT_TYPES = List.of("rustic", "classic", "community");

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_VAULT_TYPE = (source, builder) ->
            SharedSuggestionProvider.suggest(VAULT_TYPES, builder);

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_DIRECTION = (source, builder) ->
            SharedSuggestionProvider.suggest(
                    Heading.getNames().stream().filter(x -> !x.equalsIgnoreCase("UP") && !x.equalsIgnoreCase("DOWN")),
                    builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("spawn")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> spawn(ctx.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                        null, "rustic", "north"))
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(ctx -> spawn(ctx.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                "rustic", "north"))
                                        .then(Commands.argument("type", StringArgumentType.string())
                                                .suggests(SUGGEST_VAULT_TYPE)
                                                .executes(ctx -> spawn(ctx.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                                        EntityArgument.getEntities(ctx, "targets"),
                                                        StringArgumentType.getString(ctx, "type"),
                                                        "north"))
                                                .then(Commands.argument("direction", StringArgumentType.string())
                                                        .suggests(SUGGEST_DIRECTION)
                                                        .executes(ctx -> spawn(ctx.getSource(),
                                                                BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                                                EntityArgument.getEntities(ctx, "targets"),
                                                                StringArgumentType.getString(ctx, "type"),
                                                                StringArgumentType.getString(ctx, "direction")))))))));
    }

    private static int spawn(CommandSourceStack source, BlockPos pos,
                             Collection<? extends Entity> entities,
                             String typeStr, String directionStr) {
        LegacyVault.LOGGER.debug("spawn command being called.");

        Entity entity = (entities != null && !entities.isEmpty())
                ? entities.iterator().next()
                : source.getEntity();

        if (!(entity instanceof Player player)) {
            CommandResponseFormatter.formatFailure(Component.literal("Target must be a player."))
                    .forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        LegacyVault.LOGGER.debug("spawn: target player -> {}", player.getDisplayName().getString());

        Block vaultBlock = switch (typeStr.toLowerCase(Locale.ROOT)) {
            case "classic"   -> ModBlocks.CLASSIC_VAULT.get();
            case "community" -> ModBlocks.COMMUNITY_VAULT.get();
            default          -> ModBlocks.RUSTIC_VAULT.get();
        };
        boolean isCommunity = vaultBlock instanceof CommunityVaultBlock;

        Direction direction = Direction.byName(directionStr.toLowerCase(Locale.ROOT));
        if (direction == null) direction = Direction.NORTH;

        ServerLevel world = source.getLevel();
        world.setBlockAndUpdate(pos, vaultBlock.defaultBlockState().setValue(ILegacyVaultBlock.FACING, direction));
        IVaultBlockEntity blockEntity = (IVaultBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) {
            world.removeBlock(pos, false);
            LegacyVault.LOGGER.warn("spawn: block entity was null after placement at {}", pos);
            CommandResponseFormatter.formatFailure(
                    Component.literal("Vault placement failed — no block entity at " + pos.toShortString() + "."))
                    .forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        if (!isCommunity) {
            blockEntity.setOwnerUuid(player.getStringUUID());
            LegacyVault.LOGGER.debug("spawn: set vault owner -> {}", player.getStringUUID());
        }
        blockEntity.setFacing(direction);

        if (!isCommunity) {
            IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                    .orElse(null);
            if (cap == null) {
                LegacyVault.LOGGER.warn("spawn: player {} has no vault capability", player.getDisplayName().getString());
            } else {
                if (!ServerConfig.PERSONAL.unlimitedVaults.get()) {
                    int count = Math.min(cap.getCount() + 1, ServerConfig.PERSONAL.vaultsPerPlayer.get());
                    cap.setCount(count);
                    LegacyVault.LOGGER.debug("spawn: new vault count for {} -> {}", player.getDisplayName().getString(), count);
                    ServerPlayer serverPlayer = (ServerPlayer) entity;
                    VaultCountMessageToClient message = new VaultCountMessageToClient(serverPlayer.getStringUUID(), count);
                    LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
                }
                cap.getLocations().add(DimensionCoords.of(world.dimension(), pos));
            }
        }

        CommandResponseFormatter.formatSuccess(
                Component.literal("Vault spawned"),
                Component.literal(typeStr + " vault placed at " + pos.toShortString()
                        + (isCommunity ? "." : " for " + player.getScoreboardName() + ".")))
                .forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }
}

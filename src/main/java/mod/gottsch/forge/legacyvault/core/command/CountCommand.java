/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import mod.gottsch.forge.gottschcore.command.CommandResponseFormatter;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

/**
 * /legacyvault count <targets>          — reset vault count to the configured default
 * /legacyvault count <targets> <value>  — set vault count to a specific value
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class CountCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("count")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(ctx -> setCount(
                                        ctx.getSource(),
                                        EntityArgument.getEntities(ctx, "targets"),
                                        ServerConfig.PERSONAL.vaultsPerPlayer.get()))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                        .executes(ctx -> setCount(
                                                ctx.getSource(),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                IntegerArgumentType.getInteger(ctx, "value")))))));
    }

    private static int setCount(CommandSourceStack source, Collection<? extends Entity> entities, int count) {
        int updated = 0;
        for (Entity entity : entities) {
            if (!(entity instanceof Player)) continue;

            IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                    .orElse(null);
            if (cap == null) {
                LegacyVault.LOGGER.warn("count: player {} has no vault capability", entity.getName().getString());
                continue;
            }

            cap.setCount(count);
            LegacyVault.LOGGER.debug("count: set vault count for {} to {}", entity.getName().getString(), count);

            ServerPlayer serverPlayer = (ServerPlayer) entity;
            VaultCountMessageToClient message = new VaultCountMessageToClient(serverPlayer.getStringUUID(), count);
            LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
            updated++;
        }

        int finalCount = count;
        int finalUpdated = updated;
        List<Component> lines = CommandResponseFormatter.formatSuccess(
                Component.literal("Vault count updated"),
                Component.literal("Set to " + finalCount + " for " + finalUpdated + " player(s)."));
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return updated > 0 ? 1 : 0;
    }
}

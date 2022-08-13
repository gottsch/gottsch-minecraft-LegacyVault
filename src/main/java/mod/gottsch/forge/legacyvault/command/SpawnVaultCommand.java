/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.Heading;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.ILegacyVaultBlock;
import mod.gottsch.forge.legacyvault.block.LegacyVaultBlocks;
import mod.gottsch.forge.legacyvault.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.network.VaultCountMessageToClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author Mark Gottschling on Jun 5, 2021
 *
 */
public class SpawnVaultCommand {

	/**
	 * 
	 */
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_DIRECTION = (source, builder) -> {    	
		return SharedSuggestionProvider.suggest(Heading.getNames().stream().filter(x -> !x.equalsIgnoreCase("UP") && !x.equalsIgnoreCase("DOWN")), builder);
    };
    
	/**
	 * 
	 * @param dispatcher
	 */
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher
		.register(Commands.literal("vault-spawn")
				.requires(source -> {
					return source.hasPermission(2);
				})
				.then(Commands.argument("pos", BlockPosArgument.blockPos())
						.executes(source -> {
							return spawn(source.getSource(), BlockPosArgument.getLoadedBlockPos(source, "pos"), null, Direction.NORTH.name());
						})				
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(source -> {
									return spawn(source.getSource(), BlockPosArgument.getLoadedBlockPos(source, "pos"), EntityArgument.getEntities(source, "targets"), Direction.NORTH.name());							
								})
								.then(Commands.argument("direction", StringArgumentType.string())
										.suggests(SUGGEST_DIRECTION)
										.executes(source -> {
											return spawn(source.getSource(), BlockPosArgument.getLoadedBlockPos(source, "pos"), EntityArgument.getEntities(source, "targets"), StringArgumentType.getString(source, "direction"));							
										})
										)
								)
						)
				);
	}

	/**
	 * 
	 * @param source
	 * @param playerUUID
	 * @param count
	 * @return
	 */
	private static int spawn(CommandSourceStack source, BlockPos pos, Collection<? extends Entity> entities, String directionStr) {
		LegacyVault.LOGGER.debug("spawn command being called.");
		Entity entity = entities.iterator().next();
		LegacyVault.LOGGER.debug("entity -> {}", entity);

		if (entity instanceof Player) {
			LegacyVault.LOGGER.debug("player entity -> {}", ((Player)entity).getDisplayName());
			Player player = (Player)entity;
			
			// convert direction param to enum
			Direction direction = Direction.byName(directionStr);
			
			// place vault
			ServerLevel world = source.getLevel();
			world.setBlockAndUpdate(pos, LegacyVaultBlocks.VAULT.defaultBlockState().setValue(ILegacyVaultBlock.FACING, direction));
			IVaultBlockEntity blockEntity = (IVaultBlockEntity) world.getBlockEntity(pos);
			if (blockEntity == null) {
				// remove block
				world.removeBlock(pos, false);
				// TODO log
				return 1;
			}
			
			// set the owner of the chest
			if (!ServerConfig.PUBLIC_VAULT.enablePublicVault.get()) {
				blockEntity.setOwnerUuid(player.getStringUUID());
				LegacyVault.LOGGER.debug("setting vault owner -> {}", player.getStringUUID());
			}

			// update the facing
			blockEntity.setFacing(direction);

			// get capabilities
			IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
				return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
			});
			
			// increment capability size
			if (ServerConfig.GENERAL.enableLimitedVaults.get()) {
				int count = cap.getCount() + 1;
				count = count > ServerConfig.GENERAL.vaultsPerPlayer.get() ? ServerConfig.GENERAL.vaultsPerPlayer.get() : count;
				cap.setCount(count);
			
				LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
				// send state message to client
				ServerPlayer serverPlayer = (ServerPlayer)entity;
				VaultCountMessageToClient message = new VaultCountMessageToClient(serverPlayer.getStringUUID(), count);
				LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),message);
			}
			// add the vault location to capabilities
			ICoords location = new Coords(pos);
			cap.getLocations().add(location);
		}

		return 1;
	}
}
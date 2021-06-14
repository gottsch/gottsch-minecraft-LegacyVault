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
package com.someguyssoftware.legacyvault.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.Heading;
import com.someguyssoftware.gottschcore.spatial.ICoords;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.ILegacyVaultBlock;
import com.someguyssoftware.legacyvault.block.LegacyVaultBlocks;
import com.someguyssoftware.legacyvault.capability.IPlayerVaultsHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;
import com.someguyssoftware.legacyvault.network.VaultCountMessageToClient;
import com.someguyssoftware.legacyvault.tileentity.IVaultTileEntity;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Mark Gottschling on Jun 5, 2021
 *
 */
public class SpawnVaultCommand {

	/**
	 * 
	 */
    private static final SuggestionProvider<CommandSource> SUGGEST_DIRECTION = (source, builder) -> {    	
		return ISuggestionProvider.suggest(Heading.getNames().stream().filter(x -> !x.equalsIgnoreCase("UP") && !x.equalsIgnoreCase("DOWN")), builder);
    };
    
	/**
	 * 
	 * @param dispatcher
	 */
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher
		.register(Commands.literal("vault-spawn")
				.requires(source -> {
					return source.hasPermission(2);
				})
				.then(Commands.argument("pos", BlockPosArgument.blockPos())
						.executes(source -> {
							return spawn(source.getSource(), BlockPosArgument.getOrLoadBlockPos(source, "pos"), null, Direction.NORTH.name());
						})				
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(source -> {
									return spawn(source.getSource(), BlockPosArgument.getOrLoadBlockPos(source, "pos"), EntityArgument.getEntities(source, "targets"), Direction.NORTH.name());							
								})
								.then(Commands.argument("direction", StringArgumentType.string())
										.suggests(SUGGEST_DIRECTION)
										.executes(source -> {
											return spawn(source.getSource(), BlockPosArgument.getOrLoadBlockPos(source, "pos"), EntityArgument.getEntities(source, "targets"), StringArgumentType.getString(source, "direction"));							
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
	private static int spawn(CommandSource source, BlockPos pos, Collection<? extends Entity> entities, String directionStr) {
		LegacyVault.LOGGER.debug("spawn command being called.");
		Entity entity = entities.iterator().next();
		LegacyVault.LOGGER.debug("entity -> {}", entity);

		if (entity instanceof PlayerEntity) {
			LegacyVault.LOGGER.debug("player entity -> {}", ((PlayerEntity)entity).getDisplayName());
			PlayerEntity player = (PlayerEntity)entity;
			
			// convert direction param to enum
			Direction direction = Direction.byName(directionStr);
			
			// place vault
			ServerWorld world = source.getLevel();
			world.setBlockAndUpdate(pos, LegacyVaultBlocks.VAULT.defaultBlockState().setValue(ILegacyVaultBlock.FACING, direction));
			IVaultTileEntity tileEntity = (IVaultTileEntity) world.getBlockEntity(pos);
			if (tileEntity == null) {
				// remove block
				world.removeBlock(pos, false);
				// TODO log
				return 1;
			}
			
			// set the owner of the chest
			if (!Config.PUBLIC_VAULT.enablePublicVault.get()) {
				tileEntity.setOwnerUuid(player.getStringUUID());
				LegacyVault.LOGGER.debug("setting vault owner -> {}", player.getStringUUID());
			}

			// update the facing
			tileEntity.setFacing(direction);

			// get capabilities
			IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
				return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
			});
			
			// increment capability size
			if (Config.GENERAL.enableLimitedVaults.get()) {
				int count = cap.getCount() + 1;
				count = count > Config.GENERAL.vaultsPerPlayer.get() ? Config.GENERAL.vaultsPerPlayer.get() : count;
				cap.setCount(count);
			
				LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
				// send state message to client
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity)entity;
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

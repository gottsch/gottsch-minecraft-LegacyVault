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
import com.mojang.brigadier.arguments.IntegerArgumentType;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.network.VaultCountMessageToClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author Mark Gottschling on Jun 5, 2021
 *
 */
public class ResetVaultCountCommand {

	/**
	 * 
	 * @param dispatcher
	 */
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher
		.register(Commands.literal("vault-reset-count")
				.requires(source -> {
					return source.hasPermission(2);
				})			
				.then(Commands.argument("targets", EntityArgument.entities())
						.executes(source -> {
							return reset(source.getSource(), EntityArgument.getEntities(source, "targets"), ServerConfig.GENERAL.vaultsPerPlayer.get());							
						})
						.then(Commands.argument("count", IntegerArgumentType.integer())
								.executes(source -> { 
									return reset(source.getSource(), EntityArgument.getEntities(source, "targets"), IntegerArgumentType.getInteger(source, "count"));
								})
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
	private static int reset(CommandSourceStack source, Collection<? extends Entity> entities, Integer count) {
		LegacyVault.LOGGER.debug("reset command being called.");
	      for(Entity entity : entities) {
	    	  LegacyVault.LOGGER.debug("entity -> {}", entity);
	          if (entity instanceof Player) {
	        	  LegacyVault.LOGGER.debug("player entity -> {}", ((Player)entity).getDisplayName().getString());
	        	  // get capabilities
					IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
						return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
					});
					// update count
					cap.setCount(count);
					
					LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
					// send state message to client
					ServerPlayer serverPlayer = (ServerPlayer)entity;
					VaultCountMessageToClient message = new VaultCountMessageToClient(serverPlayer.getStringUUID(), count);
					LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),message);
	          }
	      }
		return 1;
	}
}
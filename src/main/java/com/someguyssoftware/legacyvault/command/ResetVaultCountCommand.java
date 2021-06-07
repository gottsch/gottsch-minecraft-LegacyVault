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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IPlayerVaultsHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;
import com.someguyssoftware.legacyvault.network.VaultCountMessageToClient;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Mark Gottschling on Jun 5, 2021
 *
 */
public class ResetVaultCountCommand {

	/**
	 * 
	 * @param dispatcher
	 */
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher
		.register(Commands.literal("vault-reset-count")
				.requires(source -> {
					return source.hasPermission(2);
				})			
				.then(Commands.argument("targets", EntityArgument.entities())
						.executes(source -> {
							return reset(source.getSource(), EntityArgument.getEntities(source, "targets"), Config.GENERAL.vaultsPerPlayer.get());							
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
	private static int reset(CommandSource source, Collection<? extends Entity> entities, Integer count) {
		LegacyVault.LOGGER.debug("reset command being called.");
	      for(Entity entity : entities) {
	    	  LegacyVault.LOGGER.debug("entity -> {}", entity);
	          if (entity instanceof PlayerEntity) {
	        	  LegacyVault.LOGGER.debug("player entity -> {}", ((PlayerEntity)entity).getDisplayName());
	        	  // get capabilities
					IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
						return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
					});
					// update count
					cap.setCount(count);
					
					LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
					// send state message to client
					ServerPlayerEntity serverPlayer = (ServerPlayerEntity)entity;
					VaultCountMessageToClient message = new VaultCountMessageToClient(serverPlayer.getStringUUID(), count);
					LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),message);
	          }
	      }
		return 1;
	}
}

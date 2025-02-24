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
package mod.gottsch.forge.legacyvault.core.eventhandler;

import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {

	// player vault files will be loaded when player joins level
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		LegacyVault.LOGGER.debug("player is logging in.");

		// load the players persistent vault inventory
		VaultPersistenceManager.load(event.getEntity());

		// update client players capabilities
		if (!ServerConfig.COMMUNITY.enabled.get() && !ServerConfig.PERSONAL.unlimitedVaults.get()) {
			// get  player capabilities
			IPlayerVaultsHandler cap = event.getEntity().getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
				return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
			});
			LegacyVault.LOGGER.debug("player cap branch count -> {}", cap.getCount());

			if (cap != null) {
				// send state message to client
				VaultCountMessageToClient message = new VaultCountMessageToClient(event.getEntity().getStringUUID(), cap.getCount());
				LegacyVault.LOGGER.debug("sending message to client -> {}", message);
				LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), message);
			}
		}		
	}

	// player vault files will be saved when player leaves level
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		LegacyVault.LOGGER.debug("player is logging out");

		VaultPersistenceManager.save(event.getEntity());

	}
}

/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.eventhandler;

import com.someguyssoftware.gottschcore.world.WorldInfo;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.network.VaultCountMessageToClient;
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
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		if (WorldInfo.isClientSide(event.getPlayer().level)) {
			return;
		}
		
		// update client players capabilities
		if (!ServerConfig.PUBLIC_VAULT.enablePublicVault.get() && ServerConfig.GENERAL.enableLimitedVaults.get()) {
			// get  player capabilities
			IPlayerVaultsHandler cap = event.getPlayer().getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
				return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
			});
			LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());

			if (cap != null) {
				// send state message to client
				VaultCountMessageToClient message = new VaultCountMessageToClient(event.getPlayer().getStringUUID(), cap.getCount());
				LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getPlayer()),message);
			}
		}		
	}
}

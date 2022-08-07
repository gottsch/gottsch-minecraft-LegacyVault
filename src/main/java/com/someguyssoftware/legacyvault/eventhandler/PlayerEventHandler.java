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
package com.someguyssoftware.legacyvault.eventhandler;

import com.someguyssoftware.gottschcore.world.WorldInfo;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof Player)) return;

//		event.addCapability(new ResourceLocation(LegacyVault.MODID, Config.CapabilityID.PLAYER_PROVIDER), new PlayerCapabilityProvider());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		if (WorldInfo.isClientSide(event.getPlayer().level)) {
			return;
		}
		
		// update client players capabilities
//		if (!Config.PUBLIC_VAULT.enablePublicVault.get() &&  Config.GENERAL.enableLimitedVaults.get()) {
//			// get  player capabilities
//			IPlayerVaultsHandler cap = event.getPlayer().getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
//				return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
//			});
//			LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
//
//			if (cap != null) {
//				// send state message to client
//				VaultCountMessageToClient message = new VaultCountMessageToClient(event.getPlayer().getStringUUID(), cap.getCount());
//				LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getPlayer()),message);
//			}
//		}
		
	}

}

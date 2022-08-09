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
package com.someguyssoftware.legacyvault.capability;


import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = Bus.MOD)
public class LegacyVaultCapabilities {
	public static Capability<IPlayerVaultsHandler> PLAYER_VAULTS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {	});
	
	/**
	 * 
	 */
	@SubscribeEvent
	public static void register(final RegisterCapabilitiesEvent event) {
		PlayerVaultsCapability.register(event);
	}
	
	/**
	 * Forge Bus Event Subscriber class
	 */
	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.FORGE)
	public static class ForgeBusSubscriber {
		/*
		 * NOTE called before entity is spawned in world
		 */
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (!(event.getObject() instanceof Player)) {
				return;
			}
			event.addCapability(PlayerVaultsCapability.ID, new PlayerVaultsCapability());
		}
	}
}

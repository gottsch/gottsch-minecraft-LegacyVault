/**
 * 
 */
package com.someguyssoftware.legacyvault.eventhandler;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.PlayerCapabilityProvider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
public class PlayerEventHandler {
	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class RegistrationHandler {
		
		@SubscribeEvent
		public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		    if (!(event.getObject() instanceof PlayerEntity)) return;

		    event.addCapability(new ResourceLocation(LegacyVault.MODID, "player_cap_provider"), new PlayerCapabilityProvider());
		}
		
		@SubscribeEvent
		public void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event) {
			
		}
	}
}

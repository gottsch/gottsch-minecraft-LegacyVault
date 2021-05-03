/**
 * 
 */
package com.someguyssoftware.legacyvault.eventhandler;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author Mark Gottschling on May 2, 2021
 *
 */
public class WorldEventHandler {

	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class RegistrationHandler {
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		public void onWorldLoad(WorldEvent.Load event) {
			/*
			 * On load of dimension 0 (overworld), initialize the loot table's context and other static loot tables
			 */
			if (WorldInfo.isServerSide((World)event.getWorld())) {
				ServerWorld world = (ServerWorld) event.getWorld();
				LegacyVault.LOGGER.info("In world load event for dimension {}", WorldInfo.getDimension(world).toString());
				if (WorldInfo.isSurfaceWorld(world)) {
					if (world.getServer().isHardcore()) {
						LegacyVault.instance.setHardCore(true);
					}
				}
			}
		}
	}
}

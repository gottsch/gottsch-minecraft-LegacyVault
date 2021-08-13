/**
 * 
 */
package com.someguyssoftware.legacyvault.eventhandler;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.exception.DbInitializationException;

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

//	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
//	public static class RegistrationHandler {
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		public void onWorldLoad(WorldEvent.Load event) {
			/*
			 * On load of dimension 0 (overworld), initialize the loot table's context and other static loot tables
			 */
			if (WorldInfo.isServerSide((World)event.getWorld())) {
				ServerWorld world = (ServerWorld) event.getWorld();
				LegacyVault.LOGGER.debug("In world load event for dimension {}", WorldInfo.getDimension(world).toString());
				if (WorldInfo.isSurfaceWorld(world)) {
					// init the db manager
					try {
						DbManager.start((Config) LegacyVault.instance.getConfig());
					} catch (DbInitializationException e) {
						LegacyVault.LOGGER.error("Unable to start database manager:", e);
					}
					
					if (world.getServer().isHardcore()) {
						LegacyVault.instance.setHardCore(true);
					}
				}
			}
			
			// TODO load database
//		}
	}
}

/**
 * 
 */
package com.someguyssoftware.legacyvault.eventhandler;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.exception.DbInitializationException;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
			if (WorldInfo.isServerSide((Level)event.getWorld())) {
				LevelAccessor world = event.getWorld();
				LegacyVault.LOGGER.debug("In world load event for dimension {}", WorldInfo.getDimension((Level) world).toString());
				if (WorldInfo.isSurfaceWorld((Level) world)) {
					// init the db manager
					try {
						DbManager.start();
					} catch (DbInitializationException e) {
						LegacyVault.LOGGER.error("Unable to start database manager:", e);
					}
					
					if (world.getServer().isHardcore()) {
						LegacyVault.instance.setHardCore(true);
					}
				}
			}
	}
}

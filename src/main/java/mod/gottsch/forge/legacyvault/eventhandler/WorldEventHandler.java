/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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

import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.db.DbManager;
import mod.gottsch.forge.legacyvault.exception.DbInitializationException;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;
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
		public void onWorldLoad(LevelEvent.Load event) {
			/*
			 * On load of dimension 0 (overworld), initialize the loot table's context and other static loot tables
			 */
			if (WorldInfo.isServerSide((Level)event.getLevel())) {
				LevelAccessor world = event.getLevel();
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

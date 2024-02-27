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
import mod.gottsch.forge.legacyvault.config.Config;
import mod.gottsch.forge.legacyvault.db.DbManager;
import mod.gottsch.forge.legacyvault.exception.DbInitializationException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.*;

/**
 * @author Mark Gottschling on May 2, 2021
 *
 */
public class WorldEventHandler {
	private static boolean isLoaded = false;

//	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
//	public static class RegistrationHandler {
		
		@SubscribeEvent(priority = EventPriority.HIGH)
		public void onWorldLoad(LevelEvent.Load event) {
			/*
			 * On load of dimension 0 (overworld), initialize the loot table's context and other static loot tables
			 */
			if (WorldInfo.isServerSide((Level)event.getLevel()) && !isLoaded) {
				LevelAccessor world = event.getLevel();
				boolean isHardCore = world.getServer().isHardcore();
				LegacyVault.LOGGER.debug("In world load event for dimension {}", WorldInfo.getDimension((Level) world).toString());
//				if (WorldInfo.isSurfaceWorld((Level) world)) {

					try {
						// init the db manager
						DbManager.start();
						// schedule db backups
						ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
						ScheduledFuture<?> resultFuture
								= executorService.scheduleAtFixedRate(DbManager::runBackupScript, 5, Config.ServerConfig.DATABASE.backupInterval.get(), TimeUnit.MINUTES);

					} catch (DbInitializationException e) {
						LegacyVault.LOGGER.error("Unable to start database manager:", e);
					}

					if (isHardCore) {
						LegacyVault.instance.setHardCore(true);
					}
					isLoaded = true;
				}

//			}
	}

	private static long lastTickTime = 0;
	@SubscribeEvent
	public static void playerTick(TickEvent.LevelTickEvent event) {

		if (event.phase == TickEvent.Phase.END) {
			if (lastTickTime == 0) {
				lastTickTime = event.level.getGameTime();
			}

			// if 5 minutes have passed
			if (event.level.getGameTime() - lastTickTime > 6000) {
				// TODO spin a new thread
				// dump the h2 db
				DbManager.runBackupScript();
			}
		}
	}
}

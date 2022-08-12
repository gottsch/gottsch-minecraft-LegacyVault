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
package mod.gottsch.forge.legacyvault.setup;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.db.DbManager;
import net.minecraftforge.event.server.ServerStoppingEvent;

/**
 * Legacy class. Move this code somewhere else
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
public class LegacyVaultSetup {

	// TODO this is stopping the world on client side, stopping the server on server side.
	public static void serverStopping(final ServerStoppingEvent event) {
		LegacyVault.LOGGER.debug("server stopping event");
		try {
			DbManager.shutdown();
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error stopping database:", e);
		}
	}
}

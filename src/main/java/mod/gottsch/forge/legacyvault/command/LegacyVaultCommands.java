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
package mod.gottsch.forge.legacyvault.command;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Mark Gottschling on Jun 5, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID)
public class LegacyVaultCommands {
	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		ResetVaultCountCommand.register(event.getDispatcher());
		GetVaultLocationsCommand.register(event.getDispatcher());
		ClearVaultLocationsCommand.register(event.getDispatcher());
		SpawnVaultCommand.register(event.getDispatcher()	);
	}
}

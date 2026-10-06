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
package mod.gottsch.neo.legacyvault.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.gottsch.neo.legacyvault.core.config.Config;
import mod.gottsch.neo.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.neo.legacyvault.core.setup.CommonSetup;
import mod.gottsch.neo.legacyvault.core.setup.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.config.ModConfig.Type;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
@Mod(LegacyVault.MOD_ID)
public class LegacyVault {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(LegacyVault.MOD_ID);

	public static final String MOD_ID = "legacyvault";
	// TODO don't like that this is here - how to access from the mods.toml file
	// part of the vault file name: each MC line keeps its own vault files
	public static final String MC_VERSION = "1.21";

	public static LegacyVault instance;
	private boolean  hardCore = false;
	
	/**
	 * 
	 */
	public LegacyVault(IEventBus modEventBus, ModContainer container) {
		instance = this;
		
		// register deferred registries
		Registration.init(modEventBus);
		LegacyVaultNetworking.register(modEventBus);
		
		// register config
		Config.register(container);

        // register the setup method for mod loading
        modEventBus.addListener(CommonSetup::init);
        modEventBus.addListener(ModConfigEvent.Loading.class, this::config);
        modEventBus.addListener(ModConfigEvent.Reloading.class, this::config);
        
//        NeoForge.EVENT_BUS.addListener(LegacyVaultSetup::serverStopping);
        // client setup is registered by ClientSetup's @EventBusSubscriber(value = Dist.CLIENT)
	}

	/**
	 * On a config event.
	 * @param event
	 */
	private void config(final ModConfigEvent event) {
		if (event.getConfig().getModId().equals(MOD_ID)) {
			if (event.getConfig().getType() == Type.SERVER) {
				if (event.getConfig().getSpec() == Config.SERVER_SPEC) {
					// prepare/format config values
					Config.init();
				} 
			}
		}
	}
	
	public void setHardCore(boolean hardCore) {
		this.hardCore  = hardCore;
	}
	
	public boolean isHardCore() {
		return hardCore;
	}
}

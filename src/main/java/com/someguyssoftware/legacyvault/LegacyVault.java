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
package com.someguyssoftware.legacyvault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.eventhandler.WorldEventHandler;
import com.someguyssoftware.legacyvault.init.LegacyVaultSetup;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
@Mod(value = LegacyVault.MODID)
@ModInfo(
		modid = LegacyVault.MODID, 
		name = LegacyVault.NAME, 
		version = LegacyVault.VERSION, 
		minecraftVersion = "1.16.5", 
		forgeVersion = "36.1.0", 
		updateJsonUrl = LegacyVault.UPDATE_JSON_URL)
@Credits(values = { "LegacyVault was first developed by Mark Gottschling on Apr 28, 2021."})
public class LegacyVault implements IMod {
	// logger
	public static Logger LOGGER = LogManager.getLogger(LegacyVault.NAME);

	// constants
	public static final String MODID = "legacyvault";
	protected static final String NAME = "Legacy Vault";
	protected static final String VERSION = "1.2.3";
	protected static final String UPDATE_JSON_URL = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-LegacyVault/1.16.5-master/update.json";

	public static LegacyVault instance;
	private static Config config;

	private boolean  hardCore = false;
	
	/**
	 * 
	 */
	public LegacyVault() {
		LegacyVault.instance = this;
		LegacyVault.config = new Config(this);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
		
		// Register the setup method for modloading
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		eventBus.addListener(LegacyVaultSetup::common);
		eventBus.addListener(LegacyVaultNetworking::common);
		MinecraftForge.EVENT_BUS.addListener(LegacyVaultSetup::serverStopping);
		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
		
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("legacyvault-common.toml"));
		Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("legacyvault-server.toml"));
	}
	

	
	public static void clientOnly() {
//		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
//		MOD_EVENT_BUS.register(ClientEventHandler.class);
	}
	
	/**
	 * ie. preint
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	private void setup(final FMLCommonSetupEvent event) {

	}


	@Override
	public IMod getInstance() {
		return LegacyVault.instance;
	}

	@Override
	public String getId() {
		return LegacyVault.MODID;
	}

	@Override
	public IConfig getConfig() {
		return LegacyVault.config;
	}

	public void setHardCore(boolean hardCore) {
		this.hardCore  = hardCore;
	}
	
	public boolean isHardCore() {
		return hardCore;
	}
}


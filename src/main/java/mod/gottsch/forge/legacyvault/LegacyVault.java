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
package mod.gottsch.forge.legacyvault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.config.IModSetup;
import com.someguyssoftware.gottschcore.mod.IMod;

import mod.gottsch.forge.legacyvault.config.Config;
import mod.gottsch.forge.legacyvault.eventhandler.WorldEventHandler;
import mod.gottsch.forge.legacyvault.setup.ClientSetup;
import mod.gottsch.forge.legacyvault.setup.CommonSetup;
import mod.gottsch.forge.legacyvault.setup.LegacyVaultSetup;
import mod.gottsch.forge.legacyvault.setup.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
@Mod(LegacyVault.MODID)
@ModInfo(
		modid = LegacyVault.MODID, 
		name = LegacyVault.NAME, 
		version = LegacyVault.VERSION, 
		minecraftVersion = LegacyVault.MC_VERSION, 
		forgeVersion = "40.1.0", 
		updateJsonUrl = LegacyVault.UPDATE_JSON_URL)
public class LegacyVault implements IMod {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(/*LegacyVault.NAME*/);

	public static final String MODID = "legacyvault";
	protected static final String NAME = "Legacy Vault";
	public static final String MC_VERSION = "1.18.2";
	protected static final String VERSION = "1.3.0";
	protected static final String UPDATE_JSON_URL = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-LegacyVault/1.18.2-master/update.json";

	public static LegacyVault instance;
	private boolean  hardCore = false;
	
	/**
	 * 
	 */
	public LegacyVault() {
		instance = this;
		
		// register deferred registries
		Registration.init();
		
		// register config
		Config.register();
		
        // register the setup method for mod loading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CommonSetup::init);
        // add logging capabilities
     	modEventBus.addListener(LegacyVault::logging);
        
        MinecraftForge.EVENT_BUS.addListener(LegacyVaultSetup::serverStopping);
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
        
        // register 'ClientSetup::init' to be called at mod setup time (client only)
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init)); 	
	}

	private static void logging(final FMLCommonSetupEvent event) {
		// add mod specific logging
		IModSetup.addRollingFileAppender(LegacyVault.instance);		
	}
	
	@Override
	public IConfig getConfig() {
		return Config.instance;
	}

	@Override
	public String getId() {
		return LegacyVault.MODID;
	}

	@Override
	public IMod getInstance() {
		return LegacyVault.instance;
	}
	
	public void setHardCore(boolean hardCore) {
		this.hardCore  = hardCore;
	}
	
	public boolean isHardCore() {
		return hardCore;
	}
}

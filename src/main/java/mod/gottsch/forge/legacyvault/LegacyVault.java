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

import com.electronwill.nightconfig.core.CommentedConfig;

import mod.gottsch.forge.legacyvault.config.Config;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.eventhandler.WorldEventHandler;
import mod.gottsch.forge.legacyvault.recipe.condition.VaultEasyDifficultyCondition;
import mod.gottsch.forge.legacyvault.setup.ClientSetup;
import mod.gottsch.forge.legacyvault.setup.CommonSetup;
import mod.gottsch.forge.legacyvault.setup.LegacyVaultSetup;
import mod.gottsch.forge.legacyvault.setup.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
@Mod(LegacyVault.MODID)
public class LegacyVault {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(LegacyVault.MODID);

	public static final String MODID = "legacyvault";
	protected static final String NAME = "Legacy Vault";
	public static final String MC_VERSION = "1.19.2";

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
        modEventBus.addListener(this::config);
        
        MinecraftForge.EVENT_BUS.addListener(LegacyVaultSetup::serverStopping);
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
        
        // register 'ClientSetup::init' to be called at mod setup time (client only)
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init)); 	
	}

	/**
	 * On a config event.
	 * @param event
	 */
	private void config(final ModConfigEvent event) {
		if (event.getConfig().getModId().equals(MODID)) {
			if (event.getConfig().getType() == Type.SERVER) {
				IConfigSpec<?> spec = event.getConfig().getSpec();

				if (spec == Config.SERVER_SPEC) {
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

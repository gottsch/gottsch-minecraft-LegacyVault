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

import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.eventhandler.WorldEventHandler;
import com.someguyssoftware.legacyvault.setup.ClientSetup;
import com.someguyssoftware.legacyvault.setup.CommonSetup;
import com.someguyssoftware.legacyvault.setup.LegacyVaultSetup;
import com.someguyssoftware.legacyvault.setup.Registration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
@Mod(LegacyVault.MODID)
public class LegacyVault {
	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = "legacyvault";
	
	public LegacyVault() {
		// register deferred registries
		Registration.init();
		
		// register config
		Config.register();
		
        // register the setup method for mod loading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // register 'CommonSetup::init' to be called at mod setup time (server and client)
        modEventBus.addListener(CommonSetup::init);
        MinecraftForge.EVENT_BUS.addListener(LegacyVaultSetup::serverStopping);
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
        
        // register 'ClientSetup::init' to be called at mod setup time (client only)
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init)); 	
	}
}

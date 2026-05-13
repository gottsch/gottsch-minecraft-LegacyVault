/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.setup;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
	
	public static void init(final FMLCommonSetupEvent event) {
		event.enqueueWork(LegacyVaultNetworking::register);
		Config.instance.addRollingFileAppender(LegacyVault.MOD_ID);
	}

	@SubscribeEvent
	public static void registemItemsToTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.RUSTIC_VAULT.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.CLASSIC_VAULT.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.COMMUNITY_VAULT.get(), TabVisibility.PARENT_AND_SEARCH_TABS);

			event.accept(ModItems.CONTRACT.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.VAULT_UPGRADE.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}
}

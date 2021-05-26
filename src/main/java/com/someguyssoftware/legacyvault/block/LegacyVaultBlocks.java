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
package com.someguyssoftware.legacyvault.block;

import com.google.common.base.Preconditions;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.item.VaultBlockItem;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class LegacyVaultBlocks {

	public static Block VAULT = null;
	
	/**
	 *
	 */
	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class RegistrationHandler {

		/**
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			VAULT = new VaultBlock(LegacyVault.MODID, Config.BlockID.VAULT_ID, Block.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.5F));
			
			final IForgeRegistry<Block> registry = event.getRegistry();
			registry.register(VAULT);
		}
		
		/**
		 * Register this mod's {@link ItemBlock}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			BlockItem itemBlock = new VaultBlockItem(VAULT, new Item.Properties().tab(ItemGroup.TAB_MISC));
			final ResourceLocation registryName = Preconditions.checkNotNull(VAULT.getRegistryName(),
					"Block %s has null registry name", VAULT);
			registry.register(itemBlock.setRegistryName(registryName));
		}
	}
}

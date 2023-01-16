/*
 * This file is part of  Treasure2.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.tags;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 
 * @author Mark Gottschling Jan 16, 2023
 *
 */
public class LegacyVaultTags {
	
	public static class Items {
		public static final TagKey<Item> EASY_RECIPE = mod(LegacyVault.MODID, "difficulty/easy");
		public static final TagKey<Item> NORMAL_RECIPE = mod(LegacyVault.MODID, "difficulty/normal");
		public static final TagKey<Item> HARD_RECIPE = mod(LegacyVault.MODID, "difficulty/hard");

		public static TagKey<Item> mod(String domain, String path) {
			return ItemTags.create(new ResourceLocation(domain, path));
		}
	}
}

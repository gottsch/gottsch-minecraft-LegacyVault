/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.datagen;

import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.item.ModItems;
import mod.gottsch.neo.legacyvault.core.tags.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mark Gottschling on Feb 2, 2025
 */
public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
	/** Treasure2 container block/item ids blacklisted from vaults. */
	private static final String[] TREASURE2_CONTAINERS = {
			"barrel_chest", "cardboard_box", "cauldron_chest", "compressor_chest", "crate_chest",
			"crystal_skull_chest", "dread_pirate_chest", "gold_skull_chest", "gold_strongbox",
			"ironbound_chest", "iron_strongbox", "milk_crate", "crate_chest_moldy", "pirate_chest",
			"safe", "skull_chest", "spider_chest", "vanilla_chest", "viking_chest", "wood_chest",
			"wither_chest"
	};

	public ItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookup,
							CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookup, blockTagProvider, LegacyVault.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		/*
		 * recipe tier selector tags — controls which crafting cost variant is active.
		 * NOTE only 1 of these tags should contain a value at a time.
		 * NOTE Items.AIR is only included here to actually construct the tag during DataGen,
		 * the air value should be removed afterwards.
		 */
		tag(ModTags.Items.NORMAL_RECIPE).add(ModItems.CLASSIC_VAULT.get());
		tag(ModTags.Items.EASY_RECIPE).add(Items.AIR);
		tag(ModTags.Items.HARD_RECIPE).add(Items.AIR);

		tag(ModTags.Items.EASY_RECIPE_BASE_MATERIALS)
				.add(Items.IRON_NUGGET)
				.add(Items.GOLD_NUGGET);
		tag(ModTags.Items.NORMAL_RECIPE_BASE_MATERIALS)
				.add(Items.IRON_INGOT)
				.add(Items.GOLD_INGOT);
		tag(ModTags.Items.HARD_RECIPE_BASE_MATERIALS)
				.add(Items.IRON_BLOCK)
				.add(Items.GOLD_INGOT);

		// vault ingredients
		tag(ModTags.Items.VAULT_CONTRACTS)
				.add(ModItems.CONTRACT.get())
				.add(ModItems.APPLICATION.get());

		// vault items whitelist — empty by default (disabled); admins populate via datapack
		tag(ModTags.Items.VAULT_ITEMS_WHITELIST);

		// vault items blacklist
		// NOTE blacklist is the default as only certain items need to be restricted
		tag(ModTags.Items.VAULT_ITEMS_BLACKLIST)
				.add(ModItems.RUSTIC_VAULT.get())
				.add(ModItems.CLASSIC_VAULT.get())
				.add(ModItems.COMMUNITY_VAULT.get())
				.add(Items.SHULKER_BOX);

		// treasure2 integration (ids as literals so Treasure2 isn't a compile dependency)
		for (String name : TREASURE2_CONTAINERS) {
			tag(ModTags.Items.VAULT_ITEMS_BLACKLIST)
					.addOptional(ResourceLocation.fromNamespaceAndPath("treasure2", name));
		}

	}
}

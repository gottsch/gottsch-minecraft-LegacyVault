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
package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.tags.ModTags;
import mod.gottsch.forge.treasure2.core.block.TreasureBlocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mark Gottschling on Feb 2, 2025
 */
public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
	public ItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookup,
							CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookup, blockTagProvider, LegacyVault.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		/*
		 * recipe difficulty selector tags
		 * NOTE only 1 of these tags should contain a value.
		 * NOTE Items.AIR is only included here to actually construct the tag during DataGen,
		 * the air value should be removed afterwards.
		 */
		tag(ModTags.Items.NORMAL_RECIPE).add(ModItems.RUSTIC_VAULT.get());
		tag(ModTags.Items.EASY_RECIPE).add(Items.AIR);
		tag(ModTags.Items.HARD_RECIPE).add(Items.AIR);

		// vault ingredients
		tag(ModTags.Items.VAULT_CONTRACTS)
				.add(ModItems.CONTRACT.get())
				.add(ModItems.APPLICATION.get());

		// vault items blacklist
		// NOTE blacklistis the default as only certain items need to be restricted
		tag(ModTags.Items.VAULT_ITEMS_BLACKLIST)
				.add(ModItems.RUSTIC_VAULT.get())
				.add(ModItems.CLASSIC_VAULT.get())
				.add(ModItems.COMMUNITY_VAULT.get())
				.add(Items.SHULKER_BOX);

		// treasure2 integration
		tag(ModTags.Items.VAULT_ITEMS_BLACKLIST)
				.addOptional(TreasureBlocks.BARREL_CHEST.getId())
				.addOptional(TreasureBlocks.CARDBOARD_BOX.getId())
				.addOptional(TreasureBlocks.CAULDRON_CHEST.getId())
				.addOptional(TreasureBlocks.COMPRESSOR_CHEST.getId())
				.addOptional(TreasureBlocks.CRATE_CHEST.getId())
				.addOptional(TreasureBlocks.CRYSTAL_SKULL_CHEST.getId())
				.addOptional(TreasureBlocks.DREAD_PIRATE_CHEST.getId())
				.addOptional(TreasureBlocks.GOLD_SKULL_CHEST.getId())
				.addOptional(TreasureBlocks.GOLD_STRONGBOX.getId())
				.addOptional(TreasureBlocks.IRONBOUND_CHEST.getId())
				.addOptional(TreasureBlocks.IRON_STRONGBOX.getId())
				.addOptional(TreasureBlocks.MILK_CRATE.getId())
				.addOptional(TreasureBlocks.MOLDY_CRATE_CHEST.getId())
				.addOptional(TreasureBlocks.PIRATE_CHEST.getId())
				.addOptional(TreasureBlocks.SAFE.getId())
				.addOptional(TreasureBlocks.SKULL_CHEST.getId())
				.addOptional(TreasureBlocks.SPIDER_CHEST.getId())
				.addOptional(TreasureBlocks.VANILLA_CHEST.getId())
				.addOptional(TreasureBlocks.VIKING_CHEST.getId())
				.addOptional(TreasureBlocks.WOOD_CHEST.getId())
				.addOptional(TreasureBlocks.WITHER_CHEST.getId());

	}
}

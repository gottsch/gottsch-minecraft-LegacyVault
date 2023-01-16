/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.inventory;

import java.util.regex.Pattern;

import mod.gottsch.forge.legacyvault.block.ILegacyVaultBlock;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Mark Gottschling on May 4, 2021
 *
 */
public class VaultSlot extends SlotItemHandler {

	private boolean active;
	
	/**
	 * 
	 * @param inventory
	 * @param index
	 * @param xPosition
	 * @param yPosition
	 */
	public VaultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.active = true;
	}
	
	/**
	 * 
	 * @param inventory
	 * @param index
	 * @param xPosition
	 * @param yPosition
	 * @param active
	 */
	public VaultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean active) {
		super(itemHandler, index, xPosition, yPosition);
		this.active = active;
	}

	@Override
	public boolean mayPlace(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
		
		Item item = itemStack.getItem();
		Block block = Block.byItem(item);

		// check if another legacy vault
		if (block instanceof ILegacyVaultBlock) {
			return false;
		}
		// check if shulker box
		if (block instanceof ShulkerBoxBlock) {
			return false;
		}
		// check if itemStack contains more items
		if (itemStack.hasTag()) {
			ListTag itemsList = itemStack.getTag().getList("Items", 10);
			if (itemsList != null && itemsList.size() > 0) {
				return false;
			}
		}

		// get the registry name of the item
//		String registryName = item.getRegistryName().toString();
		// TODO so don't like this way of getting the registry name.
		String registryName = Registry.ITEM.getKey(item).toString();

		// determine if using white lists or black lists
		if (!ServerConfig.GENERAL.inventoryWhiteList.get().isEmpty()) {
			// check against the item/block name white list
			for(Pattern pattern : ServerConfig.GENERAL.inventoryWhiteListPatterns) {
				if (registryName.matches(pattern.pattern())) {
					return true;
				}
			}
			return false;
		}
		else if (!ServerConfig.GENERAL.inventoryBlackList.get().isEmpty()) {
			// check against the item/block name black list
			for(Pattern pattern : ServerConfig.GENERAL.inventoryBlackListPatterns) {
				if (registryName.matches(pattern.pattern())) {
					return false;
				}
			}
			return true;
		}

		// check against the tags lists
		if (!ServerConfig.GENERAL.tagsWhiteList.get().isEmpty()) {
			for (String tagName : ServerConfig.GENERAL.tagsWhiteList.get()) {
				ResourceLocation location = new ResourceLocation(tagName);
				TagKey<Block> blockTag = ForgeRegistries.BLOCKS.tags().createTagKey(location);
				TagKey<Item> itemTag = ForgeRegistries.ITEMS.tags().createTagKey(location);
				if ((ForgeRegistries.BLOCKS.tags().getTag(blockTag) != null &&
						ForgeRegistries.BLOCKS.tags().getTag(blockTag).contains(block)) ||
						(ForgeRegistries.ITEMS.tags().getTag(itemTag) != null &&
								ForgeRegistries.ITEMS.tags().getTag(itemTag).contains(item))) {

					return true;
				}
			}
			return false;
		}
		else if (!ServerConfig.GENERAL.tagsBlackList.get().isEmpty()){
			for (String tagName : ServerConfig.GENERAL.tagsBlackList.get()) {				
				ResourceLocation location = new ResourceLocation(tagName);
				TagKey<Block> blockTag = ForgeRegistries.BLOCKS.tags().createTagKey(location);
				TagKey<Item> itemTag = ForgeRegistries.ITEMS.tags().createTagKey(location);
				if ((ForgeRegistries.BLOCKS.tags().getTag(blockTag) != null &&
						ForgeRegistries.BLOCKS.tags().getTag(blockTag).contains(block)) ||
						(ForgeRegistries.ITEMS.tags().getTag(itemTag) != null &&
								ForgeRegistries.ITEMS.tags().getTag(itemTag).contains(item))) {
					return false;
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public int getMaxStackSize() {
		return ServerConfig.GENERAL.stackSize.get();
	}
	
	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

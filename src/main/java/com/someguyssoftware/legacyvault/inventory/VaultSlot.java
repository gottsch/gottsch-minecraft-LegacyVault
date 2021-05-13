/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

import java.util.regex.Pattern;

import com.someguyssoftware.legacyvault.block.ILegacyVaultBlock;
import com.someguyssoftware.legacyvault.config.Config;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling on May 4, 2021
 *
 */
public class VaultSlot extends Slot {

	/**
	 * 
	 * @param inventory
	 * @param index
	 * @param xPosition
	 * @param yPosition
	 */
	public VaultSlot(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack itemStack) {
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
			ListNBT itemsList = itemStack.getTag().getList("Items", 10);
			if (itemsList != null && itemsList.size() > 0) {
				return false;
			}
		}

		// get the registry name of the item
		String registryName = item.getRegistryName().toString();

		// determine if using white lists or black lists
		if (!Config.GENERAL.inventoryWhiteList.get().isEmpty()) {
			// check against the item/block name white list
			for(Pattern pattern : Config.GENERAL.inventoryWhiteListPatterns) {
				if (registryName.matches(pattern.pattern())) {
					return true;
				}
			}			
		}
		else {
			// check against the item/block name black list
			for(Pattern pattern : Config.GENERAL.inventoryBlackListPatterns) {
				if (registryName.matches(pattern.pattern())) {
					return false;
				}
			}
		}

		// check against the tags lists
		if (!Config.GENERAL.tagsWhiteList.get().isEmpty()) {
			for (String tagName : Config.GENERAL.tagsWhiteList.get()) {
				ResourceLocation location = new ResourceLocation(tagName);
				if ((BlockTags.getAllTags().getTag(location) != null &&
						BlockTags.getAllTags().getTag(location).contains(block)) ||
						(ItemTags.getAllTags().getTag(location) != null &&
						ItemTags.getAllTags().getTag(location).contains(item))) {
					return true;
				}
			}
		}
		else {
			for (String tagName : Config.GENERAL.tagsBlackList.get()) {
				ResourceLocation location = new ResourceLocation(tagName);
				if ((BlockTags.getAllTags().getTag(location) != null &&
						BlockTags.getAllTags().getTag(location).contains(block)) ||
						(ItemTags.getAllTags().getTag(location) != null &&
						ItemTags.getAllTags().getTag(location).contains(item))) {
					return false;
				}
			}
		}
		return true;
	}
}

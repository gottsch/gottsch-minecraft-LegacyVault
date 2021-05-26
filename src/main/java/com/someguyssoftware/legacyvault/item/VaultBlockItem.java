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
package com.someguyssoftware.legacyvault.item;

import com.someguyssoftware.legacyvault.capability.IVaultBranchHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

/**
 * @author Mark Gottschling on May 25, 2021
 *
 */
public class VaultBlockItem extends BlockItem {

	/**
	 * 
	 * @param block
	 * @param properties
	 */
	public VaultBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	/**
	 * 
	 */
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {

		if (!Config.GENERAL.enablePublicVault.get()) {
			// get  player capabilities
			IVaultBranchHandler cap = context.getPlayer().getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
				return new RuntimeException("player does not have VaultBranchHandler capability.'");
			});

			if (cap.getCount() < Config.GENERAL.vaultsPerPlayer.get()) {
				ActionResultType result =  super.onItemUseFirst(stack, context);
				if (result == ActionResultType.PASS) {
					// increment capability size
					int count = cap.getCount() + 1;
					count = count > Config.GENERAL.vaultsPerPlayer.get() ? Config.GENERAL.vaultsPerPlayer.get() : count;
					cap.setCount(count);
				}
				return result;
			}
			else {
				return ActionResultType.FAIL;
			}
		}
		return super.onItemUseFirst(stack, context);
	}
}

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
package com.someguyssoftware.legacyvault.inventory;

import com.someguyssoftware.legacyvault.setup.Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * 
 * @author Mark Gottschling on Jun 19, 2022
 *
 */
public class VaultContainerMenu extends AbstractContainerMenu {
	private BlockEntity blockEntity;
	private Player playerEntity;
	private IItemHandler playerInventory;
	
	// TODO abstract it
	private int containerInventoryYPos = 18;
	private int titleYPos = containerInventoryYPos;
	
	/**
	 * 
	 * @param menuType
	 * @param containerId
	 * @param pos
	 * @param playerInventory
	 * @param player
	 */
	public VaultContainerMenu(int containerId, BlockPos pos, Inventory playerInventory, Player player) {
		super(Registration.VAULT_CONTAINER.get(), containerId);
		blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
		this.playerEntity =  player;
		this.playerInventory = new InvWrapper(playerInventory);
		
		// TODO load from the DB here
		
        if (blockEntity != null) {
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
                addSlot(new SlotItemHandler(itemHandler, 0, 64, 24)); // TODO this is the layout of the vault, 0 = index, 64 = xpos, 24 = ypos
            });
        }
//        buildContainer(playerInventory);

	}

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, Registration.VAULT.get());
    }

    // TODO move to abstract
	public void buildContainer(IItemHandler handler) {
		// build inventories
//		buildHotbar(handler);
//		buildPlayerInventory(handler);
//		buildContainerInventory();

		// TODO see McJty PowergenContainer, and merge with build...() from AbstractLegacyVault
	}
	
//	public void buildHotbar(IItemHandler handler) {
//		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
//			int slotNumber = x;
//			addSlot(new Slot(playerInventory, slotNumber, getHotbarXPos() + getSlotXSpacing() * x, getHotbarYPos()));
//		}
//	}
	
	// TODO abstract it
	public int getTitleYPos() {
		return titleYPos;
	}

	public void setTitleYPos(int titleYPos) {
		this.titleYPos = titleYPos;
	}
}

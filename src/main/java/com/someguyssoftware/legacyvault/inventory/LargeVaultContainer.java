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
package com.someguyssoftware.legacyvault.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class LargeVaultContainer extends AbstractLegacyVaultContainer {
	public static LargeVaultContainer create(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		// TODO re-introduce a SlotCount enum with sizes
		return new LargeVaultContainer(windowID, LegacyVaultContainers.LARGE_VAULT_CONTAINER_TYPE, playerInventory, VaultSlotSize.LARGE.getSize());
	}
	
	/**
	 * Client-side constructor
	 * @param windowID
	 * @param containerType
	 * @param playerInventory
	 * @param slotCount
	 */
	private LargeVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, int slotCount) {
		this(windowID, containerType, playerInventory, new Inventory(slotCount), new LegacyVaultInventory(slotCount), new LegacyVaultInventory(slotCount));

	}
	
	/**
	 * Server-side constructor
	 * @param windowID
	 * @param playerInventory
	 * @param inventory
	 */
	public LargeVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, IInventory inventory, LegacyVaultInventory vault, LegacyVaultInventory persisted) {
		super(windowID, containerType, playerInventory, inventory, vault, persisted);
        
		// open the chest (rendering)
        inventory.startOpen(playerInventory.player);
        
		// set the dimensions
		setContainerInventoryColumnCount(13);
        setContainerInventoryRowCount(7);
        setPlayerInventoryYPos(156);
        setHotbarYPos(213);
		
		// build the container
		buildContainer(playerInventory, inventory);
	}
}

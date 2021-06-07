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

import com.someguyssoftware.legacyvault.LegacyVault;

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
public class MediumVaultContainer extends AbstractLegacyVaultContainer {
	public static MediumVaultContainer create(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		return new MediumVaultContainer(windowID, LegacyVaultContainers.MEDIUM_VAULT_CONTAINER_TYPE, playerInventory, VaultSlotSize.MEDIUM.getSize());
	}
	
	/**
	 * Client-side constructor
	 * @param windowID
	 * @param containerType
	 * @param playerInventory
	 * @param slotCount
	 */
	private MediumVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, int slotCount) {
		this(windowID, containerType, playerInventory, new Inventory(slotCount));
	}
	
	/**
	 * Server-side constructor
	 * @param windowID
	 * @param playerInventory
	 * @param inventory
	 */
	public MediumVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, IInventory inventory) {
		super(windowID, containerType, playerInventory, inventory);

		// open the chest (rendering)
        inventory.startOpen(playerInventory.player);
        
		// set the dimensions
        setContainerInventoryRowCount(6);
        setPlayerInventoryYPos(138);
        setHotbarYPos(196);
		LegacyVault.LOGGER.debug("vaults remaining ypos -> {}", getVaultsRemainingYPos());
		// build the container
		buildContainer(playerInventory, inventory);
	}
}

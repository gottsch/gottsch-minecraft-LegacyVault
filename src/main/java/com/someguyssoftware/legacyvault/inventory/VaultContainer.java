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
public class VaultContainer extends AbstractLegacyVaultContainer {
	public static VaultContainer create(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		// TODO re-introduce a SlotCount enum with sizes
		return new VaultContainer(windowID, LegacyVaultContainers.STANDARD_VAULT_CONTAINER_TYPE, playerInventory, VaultSlotSize.SMALL.getSize());
	}
	
	/**
	 * Client-side constructor
	 * @param windowID
	 * @param containerType
	 * @param playerInventory
	 * @param slotCount
	 */
	private VaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, int slotCount) {
		this(windowID, containerType, playerInventory, new Inventory(slotCount), new LegacyVaultInventory(slotCount), new LegacyVaultInventory(slotCount));

	}
	
	/**
	 * Server-side constructor
	 * @param windowID
	 * @param playerInventory
	 * @param inventory
	 */
	public VaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, IInventory inventory, LegacyVaultInventory vault, LegacyVaultInventory persisted) {
		super(windowID, containerType, playerInventory, inventory, vault, persisted);
		
		// open the chest (rendering)
        inventory.startOpen(playerInventory.player);
        
		// set the dimensions
        setContainerInventoryRowCount(3);
		
		// build the container
		buildContainer(playerInventory, inventory);
	}
}

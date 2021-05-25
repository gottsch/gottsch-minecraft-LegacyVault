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
		this(windowID, containerType, playerInventory, new Inventory(slotCount));

	}
	
	/**
	 * Server-side constructor
	 * @param windowID
	 * @param playerInventory
	 * @param inventory
	 */
	public LargeVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, IInventory inventory) {
		super(windowID, containerType, playerInventory, inventory);
        
		// open the chest (rendering)
        inventory.startOpen(playerInventory.player);
        
		// set the dimensions
		setContainerInventoryColumnCount(10);
        setContainerInventoryRowCount(8);
        setPlayerInventoryYPos(174);
        setHotbarYPos(232);
		
		// build the container
		buildContainer(playerInventory, inventory);
	}
}

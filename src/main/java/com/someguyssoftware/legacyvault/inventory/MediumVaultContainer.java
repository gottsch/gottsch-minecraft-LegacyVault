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
		
		// build the container
		buildContainer(playerInventory, inventory);
	}
}

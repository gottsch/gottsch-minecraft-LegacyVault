package com.someguyssoftware.legacyvault.tileentity;

import com.someguyssoftware.legacyvault.inventory.LargeVaultContainer;
import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.inventory.VaultSlotSize;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class LargeVaultTileEntity extends AbstractVaultTileEntity {

	/**
	 * 
	 */
	public LargeVaultTileEntity() {
		super(LegacyVaultTileEntities.LARGE_VAULT_TILE_ENTITY_TYPE);
	}

	/**
	 * 
	 */
	@Override
	public int getContainerSize() {
		return VaultSlotSize.LARGE.getSize();
	}

	/**
	 * 
	 * @param windowID
	 * @param inventory
	 * @param player
	 * @return
	 */
	public Container createServerContainer(int windowID, PlayerInventory inventory, PlayerEntity player) {
		return new LargeVaultContainer(windowID, LegacyVaultContainers.LARGE_VAULT_CONTAINER_TYPE, inventory, this, this.legacyVaultInventory, this.persistedInventory);
	}
	
	///////////// IInventory Methods ///////////////////////

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot. For guis use Slot.isItemValid
	 */
	//@Override
	//public boolean isItemValidForSlot(int index, ItemStack stack) {
	//return true;
	//}

}

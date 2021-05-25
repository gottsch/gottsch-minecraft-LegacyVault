package com.someguyssoftware.legacyvault.tileentity;

import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.inventory.MediumVaultContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class MediumVaultTileEntity extends AbstractVaultTileEntity {

	/**
	 * 
	 */
	public MediumVaultTileEntity() {
		super(LegacyVaultTileEntities.MEDIUM_VAULT_TILE_ENTITY_TYPE);
	}

	/**
	 * 
	 */
	@Override
	public int getContainerSize() {
		return 54;
	}

	/**
	 * 
	 * @param windowID
	 * @param inventory
	 * @param player
	 * @return
	 */
	public Container createServerContainer(int windowID, PlayerInventory inventory, PlayerEntity player) {
		return new MediumVaultContainer(windowID, LegacyVaultContainers.MEDIUM_VAULT_CONTAINER_TYPE, inventory, this);
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

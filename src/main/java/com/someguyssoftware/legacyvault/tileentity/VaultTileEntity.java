package com.someguyssoftware.legacyvault.tileentity;

import javax.annotation.Nullable;

import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;
import com.someguyssoftware.legacyvault.inventory.VaultSlotSize;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class VaultTileEntity extends AbstractVaultTileEntity {




	/**
	 * 
	 */
	public VaultTileEntity() {
		super(LegacyVaultTileEntities.VAULT_TILE_ENTITY_TYPE);
		//		setCustomName(new TranslationTextComponent("display.vault.name"));
	}

	/**
	 * 
	 */
	@Override
	public int getContainerSize() {
		return VaultSlotSize.SMALL.getSize();
	}

	/**
	 * 
	 * @param windowID
	 * @param inventory
	 * @param player
	 * @return
	 */
	public Container createServerContainer(int windowID, PlayerInventory inventory, PlayerEntity player) {
		return new VaultContainer(windowID, LegacyVaultContainers.STANDARD_VAULT_CONTAINER_TYPE, inventory, this, this.legacyVaultInventory, this.persistedInventory);
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

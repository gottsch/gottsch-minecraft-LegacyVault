/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.inventory;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.ILegacyVaultBlock;
import mod.gottsch.forge.legacyvault.core.block.entity.AbstractVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Optional;

/**
 * 
 * @author Mark Gottschling on Jun 19, 2022
 *
 */
public abstract class VaultContainerMenu extends AbstractContainerMenu {
	// the backing block entity
	private AbstractVaultBlockEntity blockEntity;
	// the player opening the vault
	private Player playerEntity;
	// the player's inventory
	private IItemHandler playerInventory;
	// the vault's inventory (could hold a different amount - lesser - than the items list)
	private IItemHandler vaultInventory;

	// the entire inventory from persistence
	NonNullList<ItemStack> items = NonNullList.withSize(Config.General.MAX_INVENTORY_SIZE, ItemStack.EMPTY);

	///////////////
	protected final int HOTBAR_SLOT_COUNT = 9;
	protected final int PLAYER_INVENTORY_ROW_COUNT = 3;
	protected final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	protected final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	protected final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	protected final int VANILLA_FIRST_SLOT_INDEX = 0;
	protected final int CONTAINER_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

	private int menuInventoryRowCount = 3; // default value
	private int menuInventoryColumnCount = 9; // default value

	// default values for vanilla containers
	private int slotXSpacing = 18;
	private int slotYSpacing = 18;
	private int hotbarXPos = 8;
	private int hotbarYPos = 142;
	private int playerInventoryXPos = 8;
	private int playerInventoryYPos = 84;
	private int menuInventoryXPos = 8;
	private int menuInventoryYPos = 18;
	private int titleYPos = menuInventoryYPos;

	/**
	 * 
	 * @param containerId
	 * @param pos
	 * @param playerInventory
	 * @param player
	 */
	public VaultContainerMenu(MenuType<?> type, int containerId, BlockPos pos, Inventory playerInventory, Player player) {
		super(type, containerId);

		this.playerEntity =  player;
		this.playerInventory = new InvWrapper(playerInventory);
		this.vaultInventory = new InvWrapper(new SimpleContainer(ServerConfig.GENERAL.resolvedSize));

		// load from the persistence
		if (!player.level().isClientSide) {

			/*
			 * the persisted vault inventory should already be in memory in
			 * a registry. fetch it and load into vault entity inventory
			 */
			Optional<NonNullList<ItemStack>> optionalInventory = VaultPersistenceManager.get(player);
			// copy from persisted inventory to vault inventory
            optionalInventory.ifPresent(itemStacks -> copyInventoryTo(itemStacks, vaultInventory));
		}

		// get the block entity
		blockEntity = (AbstractVaultBlockEntity)player.getCommandSenderWorld().getBlockEntity(pos);
		blockEntity.openCount++;

		// setup the internal properties dependant on the size
		if (ServerConfig.GENERAL.resolvedSize == VaultSlotSize.STANDARD.getSize()) {
			// default
		}
		else if (ServerConfig.GENERAL.resolvedSize == VaultSlotSize.DOUBLE.getSize()) {
			setMenuInventoryRowCount(6);
			setPlayerInventoryYPos(138);
			setHotbarYPos(196);
		}
		else if (ServerConfig.GENERAL.resolvedSize == VaultSlotSize.XLARGE.getSize()) {
			setMenuInventoryColumnCount(13);
			setMenuInventoryRowCount(7);
			setPlayerInventoryXPos(45);
			setPlayerInventoryYPos(156);
			setHotbarXPos(45);
			setHotbarYPos(214);
		}

		buildContainer(this.playerInventory);
	}

	/**
	 *
	 */
	private void loadPersistedInventory(Player player) {

	}

	/**
	 * TODO this will have to be refactored if the size of the legacy vault > the block entity size. as it stands this will only read in x items from vault, and then save those x items back to the vault
	 * overriding the current vault items, but the vault could have had a x*n size, and so those items are lost.
	 */
	private void savePersistedInventory() {

	}

	/**
	 * TODO this can be a helper somewhere and can replace saveVaultInventory()
	 * @param source
	 * @param dest
	 */
	@Deprecated
	private void copyInventory(IItemHandler source, IItemHandler dest) {
		for (int index = 0; index < source.getSlots(); index++) {
			if (index < dest.getSlots()) {
				dest.insertItem(index, source.getStackInSlot(index), false);
			}
			else {
				break;
			}
		}	
	}

	@Deprecated
	private void copyInventoryTo(IItemHandler dest) {
		for (int index = 0; index < items.size(); index++) {
			if (index < dest.getSlots()) {
				dest.insertItem(index, items.get(index), false);
			}
			else {
				break;
			}
		}	
	}

	private void copyInventoryTo(NonNullList<ItemStack> source, IItemHandler dest) {
		for (int index = 0; index < source.size(); index++) {
			if (index < dest.getSlots()) {
				dest.insertItem(index, source.get(index), false);
			}
			else {
				break;
			}
		}
	}

	private void copyInventoryFrom(IItemHandler source, NonNullList<ItemStack> dest) {
		for (int index = 0; index < source.getSlots(); index++) {
				// NOTE cannot use add() because it will append to the end of the array,
				// and not replace/update the existing inventory slots
				dest.set(index, source.getStackInSlot(index));
		}	
	}

	/**
	 * 
	 * @param playerInventory
	 */
	public void buildContainer(IItemHandler playerInventory) {
		// build inventories
		buildHotbar(playerInventory);
		buildPlayerInventory(playerInventory);
		buildContainerInventory();
	}

	/**
	 * 
	 */
	//	@Override
	public void buildHotbar(IItemHandler inventory) {
		for (int slotNumber = 0; slotNumber < HOTBAR_SLOT_COUNT; slotNumber++) {
			addSlot(new SlotItemHandler(inventory, slotNumber, getHotbarXPos() + getSlotXSpacing() * slotNumber, getHotbarYPos()));
		}
	}

	/**
	 * 
	 * @param inventory
	 */
	//	@Override
	public void buildPlayerInventory(IItemHandler inventory) {
		/*
		 *  Add the rest of the players inventory to the gui
		 */
		for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
			for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
				int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
				int xpos = getPlayerInventoryXPos() + x * getSlotXSpacing();
				int ypos = getPlayerInventoryYPos() + y * getSlotYSpacing();
				addSlot(new SlotItemHandler(inventory, slotNumber,  xpos, ypos));
			}
		}
	}	

	/**
	 *  add the vault inventory to the gui
	 */
	public void buildContainerInventory() {		
		if (vaultInventory == null ) {
			LegacyVault.LOGGER.info("vaultInventory is null");
			return;
		}

		// build slots for display inventory
		for (int y = 0; y < getMenuInventoryRowCount(); y++) {
			for (int x = 0; x < getMenuInventoryColumnCount(); x++) {
				int slotNumber = (y * getMenuInventoryColumnCount()) + x;
				int xpos = getMenuInventoryXPos() + x * getSlotXSpacing();
				int ypos = getMenuInventoryYPos() + y * getSlotYSpacing();
				addSlot(new VaultSlot(this.vaultInventory, slotNumber, xpos, ypos));
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
//		return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, ModBlocks.RUSTIC_VAULT.get());

		// is player within distance
		if (blockEntity != null) {
			BlockPos pos = this.blockEntity.getBlockPos();
			boolean withinDistance = player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
			return withinDistance && (blockEntity != null);
		}
		return true;
	}

	@Override
	public void removed(Player player) {

		// update the open count
		blockEntity.openCount--;
		if (blockEntity.openCount < 0) {
			blockEntity.openCount = 0; 
		}

		// save to VaultPersistenceManager
		if (!player.level().isClientSide) {
			/*
			 * save inventory to persistence manager
			 */
			Optional<NonNullList<ItemStack>> optionalInventory = VaultPersistenceManager.get(player);
			// copy from persisted inventory to vault inventory
			optionalInventory.ifPresent(itemStacks -> copyInventoryFrom(vaultInventory, itemStacks));

			// save to persistence
			VaultPersistenceManager.save(player);
		}
		super.removed(player);
	}

	// TODO abstract it
	public int getTitleYPos() {
		return titleYPos;
	}

	public void setTitleYPos(int titleYPos) {
		this.titleYPos = titleYPos;
	}

	public int getHotbarXPos() {
		return hotbarXPos;
	}

	public void setHotbarXPos(int hotbarXPos) {
		this.hotbarXPos = hotbarXPos;
	}

	public int getHotbarYPos() {
		return hotbarYPos;
	}

	public void setHotbarYPos(int hotbarYPos) {
		this.hotbarYPos = hotbarYPos;
	}

	public int getSlotXSpacing() {
		return slotXSpacing;
	}

	public void setSlotXSpacing(int slotXSpacing) {
		this.slotXSpacing = slotXSpacing;
	}

	public int getPlayerInventoryXPos() {
		return playerInventoryXPos;
	}

	public void setPlayerInventoryXPos(int playerInventoryXPos) {
		this.playerInventoryXPos = playerInventoryXPos;
	}

	public int getPlayerInventoryYPos() {
		return playerInventoryYPos;
	}

	public void setPlayerInventoryYPos(int playerInventoryYPos) {
		this.playerInventoryYPos = playerInventoryYPos;
	}

	public int getSlotYSpacing() {
		return slotYSpacing;
	}

	public void setSlotYSpacing(int slotYSpacing) {
		this.slotYSpacing = slotYSpacing;
	}

	public int getMenuInventorySlotCount() {
		return getMenuInventoryRowCount() * getMenuInventoryColumnCount();
	}
	
	public int getMenuInventoryRowCount() {
		return menuInventoryRowCount;
	}

	public void setMenuInventoryRowCount(int menuInventoryRowCount) {
		this.menuInventoryRowCount = menuInventoryRowCount;
	}

	public int getMenuInventoryColumnCount() {
		return menuInventoryColumnCount;
	}

	public void setMenuInventoryColumnCount(int menuInventoryColumnCount) {
		this.menuInventoryColumnCount = menuInventoryColumnCount;
	}

	public int getMenuInventoryXPos() {
		return menuInventoryXPos;
	}

	public void setMenuInventoryXPos(int menuInventoryXPos) {
		this.menuInventoryXPos = menuInventoryXPos;
	}

	public int getMenuInventoryYPos() {
		return menuInventoryYPos;
	}

	public void setMenuInventoryYPos(int menuInventoryYPos) {
		this.menuInventoryYPos = menuInventoryYPos;
	}

	public int getVaultsRemainingYPos() {
		return getHotbarYPos() + getSlotYSpacing() + 2;
	}

	// TODO fix the backwards quick move into inventory - maybe look at T2 for how these inventories are setup
	@Override
	public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
		Slot sourceSlot = (Slot) slots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.hasItem())
			return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX
				&& sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
			/*
			 * This is a vanilla container slot so merge the stack into the tile inventory
			 */
			// first ensure that the sourcStack is a valid item for the container
//			if (!displayInventory.canPlaceItem(sourceSlotIndex, sourceStack)) {
//				return ItemStack.EMPTY;
//			}
			if (!this.moveItemStackTo(sourceStack, CONTAINER_INVENTORY_FIRST_SLOT_INDEX, CONTAINER_INVENTORY_FIRST_SLOT_INDEX + getMenuInventorySlotCount(), true)) {
				return ItemStack.EMPTY;
			}

//			if (!moveItemStackTo(sourceStack, CONTAINER_INVENTORY_FIRST_SLOT_INDEX,
//					CONTAINER_INVENTORY_FIRST_SLOT_INDEX + getContainerInventorySlotCount(), false)) {
//				return ItemStack.EMPTY;
//			}
		} else if (sourceSlotIndex >= CONTAINER_INVENTORY_FIRST_SLOT_INDEX
				&& sourceSlotIndex < CONTAINER_INVENTORY_FIRST_SLOT_INDEX + getMenuInventorySlotCount()) {
			// This is a TE slot so merge the stack into the players inventory
			if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
					false)) {
				return ItemStack.EMPTY;
			}
		} else {
			LegacyVault.LOGGER.warn("Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		if (sourceStack.isEmpty()) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}
		// If stack size == 0 (the entire stack was moved) set slot sourceInventory to
		// null
		if (sourceStack.getCount() == 0) { // getStackSize
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}

		sourceSlot.onTake(player, sourceStack); // onPickupFromSlot()
		return copyOfSourceStack;
	}
}

/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.db.entity.Account;
import com.someguyssoftware.legacyvault.enums.GameType;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftGame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public abstract class AbstractLegacyVaultContainer extends Container implements ILegacyVaultContainer {
	// stores a reference to the tile entity instance for later use
	protected  IInventory sourceInventory;
	// stores a reference to the persisted inventory
	protected LegacyVaultInventory legacyVaultInventory;

	protected final int HOTBAR_SLOT_COUNT = 9;
	protected final int PLAYER_INVENTORY_ROW_COUNT = 3;
	protected final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	protected final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	protected final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	protected final int VANILLA_FIRST_SLOT_INDEX = 0;
	protected final int CONTAINER_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

	private int containerInventoryRowCount = 3; // default value
	private int containerInventoryColumnCount = 9; // default value

	// default values for vanilla containers
	private int slotXSpacing = 18;
	private int slotYSpacing = 18;
	private int hotbarXPos = 8;
	private int hotbarYPos = 142;
	private int playerInventoryXPos = 8;
	private int playerInventoryYPos = 84;
	private int containerInventoryXPos = 8;
	private int containerInventoryYPos = 18;

	/**
	 * 
	 * @param windowID
	 * @param containerType
	 * @param playerInventory
	 * @param inventory
	 */
	public AbstractLegacyVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, IInventory inventory) {
		super(containerType, windowID);
		this.sourceInventory = inventory;
		this.legacyVaultInventory = new LegacyVaultInventory(sourceInventory.getContainerSize()); // should equal the default size of a vault OR the size of the inventory coming in

		Optional<Account> account = DbManager.getInstance().getAccount(playerInventory.player.getUUID().toString(), LegacyVault.instance.getMincraftVersion(), 
				LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());
		LegacyVault.LOGGER.info("account -> {}", account);

		if (account.isPresent()) {
			if (account.get().getInventory() != null) {
				loadVaultInventory(account.get(), legacyVaultInventory);
			}
		}
		// NOTE the parent class then calls buildContainer() in it's constructor
	}

	/**
	 * 
	 * @param account
	 * @param legacyVaultInventory2
	 */
	private void loadVaultInventory(Account account, LegacyVaultInventory legacyVaultInventory2) {

		ByteArrayInputStream bais = new ByteArrayInputStream(account.getInventory());
		CompoundNBT  compound = null;
		try {
			compound =  CompressedStreamTools.readCompressed(bais);
			ItemStackHelper.loadAllItems(compound, legacyVaultInventory.getItems());
		} catch (IOException e) {
			LegacyVault.LOGGER.error("an error occurred attempting to load vault inventory from persistence ->", e);
		}
	}

	/**
	 * 
	 * @param account
	 * @param legacyVaultInventory2
	 */
	private void saveVaultInventory(Account account, LegacyVaultInventory inventory) {
		CompoundNBT compound = new CompoundNBT();
		try {
			ItemStackHelper.saveAllItems(compound, inventory.getItems());
			LegacyVault.LOGGER.info("saving list -> {}", compound.getList("Items", 10));

		} catch (Exception e) {
			LegacyVault.LOGGER.error("error writing inventory to NBT ->", e);
			return;
		}

		// convert to a byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			CompressedStreamTools.writeCompressed(compound, baos);
		} catch (IOException e) {
			LegacyVault.LOGGER.error("error compressing inventory stream to compound ->", e);
			return;
		}
		baos.toByteArray();

		account.setInventory(baos.toByteArray());
		DbManager.getInstance().saveAccount(account);
	}
	
	/**
	 * 
	 * @param playerInventory
	 * @param inventory
	 */
	public void buildContainer(PlayerInventory playerInventory, IInventory inventory) {
		// TODO this is not necessary. just ensure that you only copy the first getContainerInventorySlotCount from inventory
		// ensure the  container's slot count is the same size of the backing IInventory
		if (getContainerInventorySlotCount()  != inventory.getContainerSize()) {
			LegacyVault.LOGGER.error("Mismatched slot count in Container(" + getContainerInventorySlotCount()
			+ ") and TileInventory (" + inventory.getContainerSize()+")");
		}

		buildHotbar(playerInventory);
		buildPlayerInventory(playerInventory);
		buildContainerInventory();
	}

	// TODO need to change all slots to LegacyVault slots or change isAllowed()? method that prevents other LegacyVaults from being stored
	/**
	 * 
	 */
	public void buildHotbar(PlayerInventory playerInventory) {
		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
			int slotNumber = x;
			addSlot(new Slot(playerInventory, slotNumber, getHotbarXPos() + getSlotXSpacing() * x, getHotbarYPos()));
		}
	}

	/**
	 * 
	 * @param playerInventory
	 */
	public void buildPlayerInventory(PlayerInventory playerInventory) {
		/*
		 *  Add the rest of the players inventory to the gui
		 */
		for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
			for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
				int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
				int xpos = getPlayerInventoryXPos() + x * getSlotXSpacing();
				int ypos = getPlayerInventoryYPos() + y * getSlotYSpacing();
				addSlot(new Slot(playerInventory, slotNumber,  xpos, ypos));
			}
		}
	}

	/**
	 * 
	 */
	public void buildContainerInventory() {		
		/*
		 *  Add the tile inventory container to the gui
		 */
		if (legacyVaultInventory != null ) {
			LegacyVault.LOGGER.info("legacyVaultInventory ->{}", legacyVaultInventory.getContainerSize());
		}
		else {
			LegacyVault.LOGGER.info("legacyVaultInventory is null");
			return;
		}
		for (int y = 0; y < getContainerInventoryRowCount(); y++) {
			for (int x = 0; x < getContainerInventoryColumnCount(); x++) {
				int slotNumber = /*CONTAINER_INVENTORY_FIRST_SLOT_INDEX +*/ (y * getContainerInventoryColumnCount()) + x;
				int xpos = getContainerInventoryXPos() + x * getSlotXSpacing();
				int ypos = getContainerInventoryYPos() + y * getSlotYSpacing();
				addSlot(new VaultSlot(this.legacyVaultInventory, slotNumber, xpos, ypos));
			}
		}
	}

	// This is where you specify what happens when a player shift clicks a slot in the gui
	//  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
	//    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
	//    position in the TileEntity inventory)
	// At the very least you must override this and return EMPTY_ITEM or the game will crash when the player shift clicks a slot
	// returns EMPTY_ITEM if the source slot is empty, or if none of the the source slot items could be moved
	//   otherwise, returns a copy of the source stack
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int sourceSlotIndex) {
		Slot sourceSlot = (Slot)slots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
			/*
			 *  This is a vanilla container slot so merge the stack into the tile inventory
			 */
			// first ensure that the sourcStack is a valid item for the container
			if (!sourceInventory.canPlaceItem(sourceSlotIndex, sourceStack)) {
				return ItemStack.EMPTY;
			}

			if (!moveItemStackTo(sourceStack, CONTAINER_INVENTORY_FIRST_SLOT_INDEX, CONTAINER_INVENTORY_FIRST_SLOT_INDEX + getContainerInventorySlotCount(), false)){
				return ItemStack.EMPTY;
			}
		} else if (sourceSlotIndex >= CONTAINER_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < CONTAINER_INVENTORY_FIRST_SLOT_INDEX + getContainerInventorySlotCount()) {
			// This is a TE slot so merge the stack into the players inventory
			if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			LegacyVault.LOGGER.warn("Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot sourceInventory to null
		if (sourceStack.getCount() == 0) {  // getStackSize
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}

		sourceSlot.onTake(player, sourceStack);  //onPickupFromSlot()
		return copyOfSourceStack;
	}

	/*
	 * pass the close container message to the tileEntity
	 * @see ContainerChest
	 * @see TileEntityChest
	 * @see net.minecraft.inventory.Container#onContainerClosed(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public void removed(PlayerEntity player) {
		if (!player.level.isClientSide) {
			// write to DB
			Optional<Account> account = DbManager.getInstance().getAccount(player.getUUID().toString(), LegacyVault.instance.getMincraftVersion(), 
					LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());
			
			if (account.isPresent()) {
				saveVaultInventory(account.get(), this.legacyVaultInventory);
			}
		}
		super.removed(player);
		this.sourceInventory.stopOpen(player);
	}

	public int getInventorySize() {
		return sourceInventory.getContainerSize();
	}
	
	/**
	 * 
	 */
	@Override
	public boolean stillValid(PlayerEntity player) {
		return sourceInventory.stillValid(player);
	}

	/**
	 * @return the containerInventoryRowCount
	 */
	public int getContainerInventoryRowCount() {
		return containerInventoryRowCount;
	}

	/**
	 * @param containerInventoryRowCount the containerInventoryRowCount to set
	 */
	public void setContainerInventoryRowCount(int containerInventoryRowCount) {
		this.containerInventoryRowCount = containerInventoryRowCount;
	}
	/**
	 * @return the containerInventoruColumnCount
	 */
	public int getContainerInventoryColumnCount() {
		return containerInventoryColumnCount;
	}
	/**
	 * @param containerInventoruColumnCount the containerInventoruColumnCount to set
	 */
	public void setContainerInventoryColumnCount(int containerInventoryColumnCount) {
		this.containerInventoryColumnCount = containerInventoryColumnCount;
	}

	/**
	 * @return the containerInventorySlotCount
	 */
	public int getContainerInventorySlotCount() {
		return getContainerInventoryRowCount() * getContainerInventoryColumnCount();
	}

	/**
	 * @return the slotXSpacing
	 */
	public int getSlotXSpacing() {
		return slotXSpacing;
	}

	/**
	 * @param slotXSpacing the slotXSpacing to set
	 */
	public void setSlotXSpacing(int slotXSpacing) {
		this.slotXSpacing = slotXSpacing;
	}

	/**
	 * @return the slotYSpacing
	 */
	public int getSlotYSpacing() {
		return slotYSpacing;
	}

	/**
	 * @param slotYSpacing the slotYSpacing to set
	 */
	public void setSlotYSpacing(int slotYSpacing) {
		this.slotYSpacing = slotYSpacing;
	}

	/**
	 * @return the hotbarYPos
	 */
	public int getHotbarYPos() {
		return hotbarYPos;
	}

	/**
	 * @param hotbarYPos the hotbarYPos to set
	 */
	public void setHotbarYPos(int hotbarYPos) {
		this.hotbarYPos = hotbarYPos;
	}

	/**
	 * @return the hotbarXPos
	 */
	public int getHotbarXPos() {
		return hotbarXPos;
	}

	/**
	 * @param hotbarXPos the hotbarXPos to set
	 */
	public void setHotbarXPos(int hotbarXPos) {
		this.hotbarXPos = hotbarXPos;
	}

	/**
	 * @return the playerInventoryXPos
	 */
	public int getPlayerInventoryXPos() {
		return playerInventoryXPos;
	}

	/**
	 * @param playerInventoryXPos the playerInventoryXPos to set
	 */
	public void setPlayerInventoryXPos(int playerInventoryXPos) {
		this.playerInventoryXPos = playerInventoryXPos;
	}

	/**
	 * @return the playerInventoryYPos
	 */
	public int getPlayerInventoryYPos() {
		return playerInventoryYPos;
	}

	/**
	 * @param playerInventoryYPos the playerInventoryYPos to set
	 */
	public void setPlayerInventoryYPos(int playerInventoryYPos) {
		this.playerInventoryYPos = playerInventoryYPos;
	}

	/**
	 * @return the containerInventoryXPos
	 */
	public int getContainerInventoryXPos() {
		return containerInventoryXPos;
	}

	/**
	 * @param containerInventoryXPos the containerInventoryXPos to set
	 */
	public void setContainerInventoryXPos(int containerInventoryXPos) {
		this.containerInventoryXPos = containerInventoryXPos;
	}

	/**
	 * @return the containerInventoryYPos
	 */
	public int getContainerInventoryYPos() {
		return containerInventoryYPos;
	}

	/**
	 * @param containerInventoryYPos the containerInventoryYPos to set
	 */
	public void setContainerInventoryYPos(int containerInventoryYPos) {
		this.containerInventoryYPos = containerInventoryYPos;
	}

	@Override
	public IInventory getContents() {
		return sourceInventory;
	}

	@Override
	public void setContents(IInventory contents) {
		this.sourceInventory = contents;
	}

	@Override
	public LegacyVaultInventory getVaultInventory() {
		return legacyVaultInventory;
	}

	@Override
	public void setVaultInventory(LegacyVaultInventory legacyVaultInventory) {
		this.legacyVaultInventory = legacyVaultInventory;
	}
}

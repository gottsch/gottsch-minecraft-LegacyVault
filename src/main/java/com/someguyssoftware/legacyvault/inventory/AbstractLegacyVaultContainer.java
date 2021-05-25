/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.db.entity.Account;
import com.someguyssoftware.legacyvault.enums.GameType;

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

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public abstract class AbstractLegacyVaultContainer extends Container implements ILegacyVaultContainer {
	// stores a reference to the tile entity instance for later use (provides max items to scroll through)
	protected  IInventory sourceInventory;
	// temporary storage for items from vault inventory to display. currently, in non-scrolling screen, it should match the size of the source inventory.
	protected IInventory displayInventory;
	// a working subset of the persisted  inventory. it is the same size as the source inventory
	protected LegacyVaultInventory legacyVaultInventory; // TODO this needs to copy sourceInventory.size items from persisted. 
	// stores a reference to the persisted inventory. the persisted inventory may have a larger size than the current source inventory
	private LegacyVaultInventory persistedInventory; // TODO this is populated from load() and updated from legacyVaultInventory on save()

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
	private int currentRow = 0;

	/**
	 * NOTE: cannot use getContainerInventory...Count() as they will be the default vaules because the child class hasn't been able to update the values yet.
	 * @param windowID
	 * @param containerType
	 * @param playerInventory
	 * @param inventory
	 */
	public AbstractLegacyVaultContainer(int windowID, ContainerType<?> containerType, PlayerInventory playerInventory, IInventory inventory) {
		super(containerType, windowID);
		this.sourceInventory = inventory;		
		// NOTE the parent class then calls buildContainer() in it's constructor
	}

	/**
	 * TODO this will have to be refactored if the size of the legacy vault > the tile entity size
	 * @param account
	 * @param inventory
	 */
	private void loadPersistedInventory(Account account, LegacyVaultInventory inventory) {

		ByteArrayInputStream bais = new ByteArrayInputStream(account.getInventory());
		CompoundNBT  compound = null;
		try {
			compound =  CompressedStreamTools.readCompressed(bais);
			ItemStackHelper.loadAllItems(compound, inventory.getItems());
		} catch (IOException e) {
			LegacyVault.LOGGER.error("an error occurred attempting to load vault inventory from persistence ->", e);
		}
	}

	/**
	 * TODO this will have to be refactored if the size of the legacy valut > the tile entity size. as it stands this will only read in x items from vault, and then save those x items back to the vault
	 * overriding the current vault items, but the vault could have had a x*n size, and so those items are lost.
	 * @param account
	 * @param legacyVaultInventory2
	 */
	private void savePersistedInventory(Account account, LegacyVaultInventory inventory) {
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
	 */
	private void loadVaultInventory() {
		for (int index = 0; index < legacyVaultInventory.getContainerSize(); index++) {
			legacyVaultInventory.setItem(index, persistedInventory.getItem(index));
		}		
	}

	/**
	 * 
	 */
	private void saveVaultInventory() {
		for (int index = 0; index < legacyVaultInventory.getContainerSize(); index++) {
			persistedInventory.setItem(index, legacyVaultInventory.getItem(index));
		}	
	}
	
	/**
	 * 
	 * @param row
	 */
	private void loadDisplayInventory(int row) {		
		if (row != this.currentRow) {
			LegacyVault.LOGGER.debug("saving display inventory first.");
			saveDisplayInventory();
		}
		
		// copy from vault inventory to display inventory
		int fromVaultIndex =  row * getContainerInventoryColumnCount(); // TODO this could potentially be wrong if called from constructor
//		LegacyVault.LOGGER.debug("fromVaultIndex -> {}", fromVaultIndex);
//		LegacyVault.LOGGER.debug("display container size -> {}, legacy container size -> {}", this.displayInventory.getContainerSize(), legacyVaultInventory.getContainerSize());
		for (int index = 0; index < displayInventory.getContainerSize(); index++) {
			if (index < this.displayInventory.getContainerSize() && index < legacyVaultInventory.getContainerSize()) {
				this.displayInventory.setItem(index, legacyVaultInventory.getItem(fromVaultIndex + index));
			}
		}
		
		// need to save the current row to properly update the vault inventory at the right positions (either an index or row)
		this.currentRow = row; // TODO is this the right place to save current row ?
	}
	
	/**
	 * 
	 * @param row
	 */
	private void saveDisplayInventory() {
		int toVaultIndex =  this.currentRow * getContainerInventoryColumnCount();
//		LegacyVault.LOGGER.debug("save display inventory: currentRow ->{}, toVaultIndex -> {}", currentRow, toVaultIndex);
		// first need to sync what is currently in display inventory back into vault inventory
		for (int index = 0; index < getContainerInventorySlotCount(); index++) {
			if (index < displayInventory.getContainerSize() && index < legacyVaultInventory.getContainerSize()) {
				legacyVaultInventory.setItem(toVaultIndex + index, displayInventory.getItem(index));
			}
		}
	}

	/**
	 * 
	 * @param playerInventory
	 * @param inventory
	 */
	public void buildContainer(PlayerInventory playerInventory, IInventory inventory) {
		// setup container
		this.displayInventory = new Inventory(inventory.getContainerSize());
//		LegacyVault.LOGGER.info("container -> {}", this.getClass().getSimpleName());
//		LegacyVault.LOGGER.debug("column count ->  {}, row count -> {}", getContainerInventoryColumnCount(), getContainerInventoryRowCount());
//		LegacyVault.LOGGER.debug("inventory slot count (this should be 27 (default)) -> {}", getContainerInventorySlotCount());
//		LegacyVault.LOGGER.info("sourceInventory -> {}, size -> {}", sourceInventory.getClass().getSimpleName(), inventory.getContainerSize());
//		LegacyVault.LOGGER.info("displayInventory -> {}, size -> {}", displayInventory.getClass().getSimpleName(), displayInventory.getContainerSize());
		this.legacyVaultInventory = new LegacyVaultInventory(sourceInventory.getContainerSize()); // should equal the default size of a vault OR the size of the inventory coming in
		this.persistedInventory = new LegacyVaultInventory(Config.GENERAL.inventorySize.get());
//		LegacyVault.LOGGER.info("vaultInventory -> {}, size -> {}", legacyVaultInventory.getClass().getSimpleName(), legacyVaultInventory.getContainerSize());
//		LegacyVault.LOGGER.info("persistedInventory -> {}, size -> {}", persistedInventory.getClass().getSimpleName(), persistedInventory.getContainerSize());
		
		Optional<Account> account = DbManager.getInstance().getAccount(playerInventory.player.getUUID().toString(), LegacyVault.instance.getMincraftVersion(), 
				LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());
//		LegacyVault.LOGGER.info("account -> {}", account);

		if (account.isPresent()) {
			if (account.get().getInventory() != null) {
				loadPersistedInventory(account.get(), persistedInventory);
			}
		}
//		LegacyVault.LOGGER.info("persistedInventory -> {}", persistedInventory);
		// init vault inventory
		loadVaultInventory();
//		LegacyVault.LOGGER.info("vaultInventory -> {}", legacyVaultInventory);
		
		// init display inventory from vault inventory
		loadDisplayInventory(this.currentRow);
		
		// build inventories
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

		// build slots for display inventory
		for (int y = 0; y < getContainerInventoryRowCount(); y++) {
			for (int x = 0; x < getContainerInventoryColumnCount(); x++) {
				int slotNumber = (y * getContainerInventoryColumnCount()) + x;
				int xpos = getContainerInventoryXPos() + x * getSlotXSpacing();
				int ypos = getContainerInventoryYPos() + y * getSlotYSpacing();
//				addSlot(new VaultSlot(this.legacyVaultInventory, slotNumber, xpos, ypos));
//				LegacyVault.LOGGER.debug("building slot -> {}, y -> {}, x-> {}, displayInventory.size -> {}", slotNumber, y, x, this.displayInventory.getContainerSize());
				addSlot(new VaultSlot(this.displayInventory, slotNumber, xpos, ypos));
			}
		}
	}
	   
	/**
	 * 
	 * @param row
	 */
	public void updateContainerInventory(int row) {
		if (row != currentRow) {
			loadDisplayInventory(row);
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
		// TODO first need to sync what is currently in display inventory back into vault inventory
		
		if (!player.level.isClientSide) {
			// copy from display inventory to vault inventory
			saveDisplayInventory();
			
			// TODO copy from vault inventory to persisted inventory
			saveVaultInventory();
			
			// fetch the players account from persistence
			Optional<Account> account = DbManager.getInstance().getAccount(player.getUUID().toString(), LegacyVault.instance.getMincraftVersion(), 
					LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());

			// write to DB
			if (account.isPresent()) {
				savePersistedInventory(account.get(), this.persistedInventory);
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
	 * The displayable (gui / active) slot count
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

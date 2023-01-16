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
package mod.gottsch.forge.legacyvault.inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.entity.VaultBlockEntity;
import mod.gottsch.forge.legacyvault.config.Config;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.db.DbManager;
import mod.gottsch.forge.legacyvault.db.entity.Account;
import mod.gottsch.forge.legacyvault.enums.GameType;
import mod.gottsch.forge.legacyvault.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * 
 * @author Mark Gottschling on Jun 19, 2022
 *
 */
public class VaultContainerMenu extends AbstractContainerMenu {
	// the backing block entity
	private VaultBlockEntity blockEntity;
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
	 * @param menuType
	 * @param containerId
	 * @param pos
	 * @param playerInventory
	 * @param player
	 */
	public VaultContainerMenu(int containerId, BlockPos pos, Inventory playerInventory, Player player) {
		super(Registration.VAULT_CONTAINER.get(), containerId);

		this.playerEntity =  player;
		this.playerInventory = new InvWrapper(playerInventory);
		this.vaultInventory = new InvWrapper(new SimpleContainer(ServerConfig.GENERAL.inventorySize.get()));
				
		// load from the DB
		if (!player.level.isClientSide) {
		Optional<Account> account = DbManager.getInstance().getAccount(playerInventory.player.getUUID().toString(), LegacyVault.MC_VERSION, 
				LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());
		LegacyVault.LOGGER.debug("account -> {}", account);
		
		// copy to persisted inventory
		if (account.isPresent()) {
			if (account.get().getInventory() != null) {
				loadPersistedInventory(account.get());
				// copy from persisted to vault
				copyInventoryTo(vaultInventory);
			}
		}
		}
		
		// get the block entity
		blockEntity = (VaultBlockEntity)player.getCommandSenderWorld().getBlockEntity(pos);
		blockEntity.openCount++;
		
		// TODO really need to change property to text value -> SMALL, MEDIUM, LARGE
		// setup the internal properties dependant on the size
		if (ServerConfig.GENERAL.inventorySize.get() <= VaultSlotSize.SMALL.getSize()) {
			// default
		}
		else if (ServerConfig.GENERAL.inventorySize.get() <= VaultSlotSize.MEDIUM.getSize()) {
	        setMenuInventoryRowCount(6);
	        setPlayerInventoryYPos(138);
	        setHotbarYPos(196);
		}
		else if (ServerConfig.GENERAL.inventorySize.get() <= VaultSlotSize.LARGE.getSize()) {
			setMenuInventoryColumnCount(13);
	        setMenuInventoryRowCount(7);
	        setPlayerInventoryXPos(45);
	        setPlayerInventoryYPos(155);
	        setHotbarXPos(45);
	        setHotbarYPos(214);
		}
		
		buildContainer(this.playerInventory);
	}

	/**
	 * @param account
	 * @param inventory
	 */
	private void loadPersistedInventory(Account account) {

		ByteArrayInputStream bais = new ByteArrayInputStream(account.getInventory());
		CompoundTag  compound = null;
		try {			
			compound =  NbtIo.readCompressed(bais);
			ContainerHelper.loadAllItems(compound, this.items);
		} catch (IOException e) {
			LegacyVault.LOGGER.error("an error occurred attempting to load vault inventory from persistence ->", e);
		}
	}
	
	/**
	 * TODO this will have to be refactored if the size of the legacy valut > the block entity size. as it stands this will only read in x items from vault, and then save those x items back to the vault
	 * overriding the current vault items, but the vault could have had a x*n size, and so those items are lost.
	 * @param account
	 * @param legacyVaultInventory2
	 */
	private void savePersistedInventory(Account account) {
		CompoundTag compound = new CompoundTag();
		try {
			LegacyVault.LOGGER.debug("saving inventory items -> {}", items);
			// copy from vault inventory to items list
			copyInventoryFrom(vaultInventory);
			// copy items list to nbt
			ContainerHelper.saveAllItems(compound, items);
			LegacyVault.LOGGER.debug("saving compound items -> {}", compound.getList("Items", 10));

		} catch (Exception e) {
			LegacyVault.LOGGER.error("error writing inventory to NBT ->", e);
			return;
		}

		// convert to a byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			NbtIo.writeCompressed(compound, baos);
		} catch (IOException e) {
			LegacyVault.LOGGER.error("error compressing inventory stream to compound ->", e);
			return;
		}
		baos.toByteArray();

		account.setInventory(baos.toByteArray());
		DbManager.getInstance().saveAccount(account);
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
	
	private void copyInventoryFrom(IItemHandler source) {
		for (int index = 0; index < source.getSlots(); index++) {
			items.set(index, source.getStackInSlot(index));
		}	
	}
	
	/**
	 * 
	 * @param playerInventory
	 * @param inventory
	 */
	public void buildContainer(IItemHandler playerInventory) {
		// build inventories
		buildHotbar(playerInventory);
		buildPlayerInventory(playerInventory);
		buildContainerInventory();
	}
	
	// TODO need to change all slots to LegacyVault slots or change isAllowed()? method that prevents other LegacyVaults from being stored
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
	 * @param playerInventory
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
	 *  Add the vault inventory to the gui
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
	public boolean stillValid(Player playerIn) {
		return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, Registration.VAULT.get());
	}

	@Override
	public void removed(Player player) {

		// update the open count
		blockEntity.openCount--;
		if (blockEntity.openCount < 0) {
			blockEntity.openCount = 0; 
		}
		
		// fetch the players account from persistence
		Optional<Account> account = DbManager.getInstance().getAccount(player.getUUID().toString(), LegacyVault.MC_VERSION, 
				LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());

		savePersistedInventory(account.get());

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

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		// TODO Auto-generated method stub
		// TODO look at Treasure2 1.18.2
		return null;
	}
}

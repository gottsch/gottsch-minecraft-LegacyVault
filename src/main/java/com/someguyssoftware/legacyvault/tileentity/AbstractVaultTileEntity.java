/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.legacyvault.tileentity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.Nullable;

import com.someguyssoftware.gottschcore.tileentity.AbstractModTileEntity;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.VaultBlock;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.db.entity.Account;
import com.someguyssoftware.legacyvault.enums.GameType;
import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.inventory.LegacyVaultInventory;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author Mark Gottschling on May 23, 2021
 *
 */
public abstract class AbstractVaultTileEntity  extends AbstractModTileEntity implements IVaultTileEntity, IInventory, ITickableTileEntity, INamedContainerProvider, INameable {
	/*
	 * The FACING index value of the VaultBlock - why is this needed still?
	 */
	private Direction facing;
	
	private String ownerUuid;

	protected float handleAngle;
	protected float prevHandleAngle;
	
	private float boltPosition = 0F;
	protected float prevBoltPosition = 0F;

	protected boolean isHandleOpen = false;
	protected boolean isHandleClosed = true;
	protected boolean isBoltOpen = false;
	protected boolean isBoltClosed = false;
	protected boolean isLidOpen = false;
	protected boolean isLidClosed = false;
	
	/*
	 * Vanilla properties for controlling the lid
	 */
	/** The current angle of the lid (between 0 and 1) */
	private float lidAngle;
	/** The angle of the lid last tick */
	private float prevLidAngle;
	/** The number of players currently using this chest */
	public int openCount;
	/** Server sync counter (once per 20 ticks) */
	public int ticksSinceSync;
	
	/** IInventory properties */
	private NonNullList<ItemStack> items = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
	
	private ITextComponent customName;
	
	/** a working subset of the persisted  inventory. it is the same size as the source inventory */
	LegacyVaultInventory legacyVaultInventory;
	/** stores a reference to the persisted inventory. the persisted inventory may have a larger size than the current source inventory */
	LegacyVaultInventory persistedInventory;

	/**
	 * 
	 * @param type
	 */
	public AbstractVaultTileEntity(TileEntityType<?> type) {
		super(type);
		setFacing(Direction.NORTH.get3DDataValue());
	}
		
	abstract public int getContainerSize();
	
	/**
	 * The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the SERVER only
	 * @param windowID
	 * @param playerInventory
	 * @param playerEntity
	 * @return
	 */
	@Nullable
	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		/**
		 * The tile entity loads the inventory from the DB when the user attempts to open the container.
		 * See AbstractLegacyVaultContainer for saving the inventory back to the DB.
		 */
		
		legacyVaultInventory = new LegacyVaultInventory(this.getContainerSize()); // should equal the default size of a vault OR the size of the inventory coming in
		persistedInventory = new LegacyVaultInventory(Config.GENERAL.inventorySize.get());

		Optional<Account> account = DbManager.getInstance().getAccount(playerInventory.player.getUUID().toString(), LegacyVault.instance.getMincraftVersion(), 
				LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());
		LegacyVault.LOGGER.debug("account -> {}", account);
		
		if (account.isPresent()) {
			if (account.get().getInventory() != null) {
				loadPersistedInventory(account.get(), persistedInventory);
			}
		}
		
		LegacyVault.LOGGER.info("persistedInventory -> {}", persistedInventory);
		// init vault inventory
		loadVaultInventory(persistedInventory, legacyVaultInventory);
		LegacyVault.LOGGER.info("vaultInventory -> {}", legacyVaultInventory);
		
		// init display inventory from vault inventory
		loadDisplayInventory(legacyVaultInventory, this);
		
		return createServerContainer(windowID, playerInventory, playerEntity);
	}
	
	abstract public Container createServerContainer(int windowID, PlayerInventory inventory, PlayerEntity player);
	
	/**
	 * 
	 * @param persisted
	 * @param vault
	 */
	protected void loadVaultInventory(IInventory persisted, IInventory vault) {
		for (int index = 0; index < vault.getContainerSize(); index++) {
			vault.setItem(index, persisted.getItem(index));
		}		
	}
	
	/**
	 * 
	 * @param vault
	 * @param display
	 */
	protected void loadDisplayInventory(IInventory vault, IInventory display) {		
		for (int index = 0; index < display.getContainerSize(); index++) {
			if (index < display.getContainerSize() && index < vault.getContainerSize()) {
				display.setItem(index, vault.getItem(index));
			}
		}
	}
	
	/**
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
	 * 
	 */
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		try {
			compound.putInt("facing", getFacing().get3DDataValue());
			if (getOwnerUuid() !=null) {
				compound.putString("ownerUuid", getOwnerUuid());
			}
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error writing to NBT:", e);
		}

		return compound;
	}

	/**
	 * 
	 */
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		try {
			if (compound.contains("facing")) {
				this.setFacing(compound.getInt("facing"));
			}
			if (compound.contains("ownerUuid")) {
				this.setOwnerUuid(compound.getString("ownerUuid"));
			}
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error reading to NBT:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public void tick() {
		updateOpenCount(++this.ticksSinceSync);
		updateEntityState();
	}

	/**
	 * NOTE initialize non-zero value of this.openCount is set in startOpen()
	 * @param ticksSinceSync
	 * @return
	 */
	public void updateOpenCount(int ticksSinceSync) {
		int x = getBlockPos().getX();
		int y = getBlockPos().getY();
		int z = getBlockPos().getZ();
		
		if (WorldInfo.isServerSide(this.getLevel()) && this.openCount != 0
				&& (this.ticksSinceSync + x + y + z) % 200 == 0) {
			this.openCount = 0;
			float radius = 5.0F;

			for(PlayerEntity player : getLevel().getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB((double)((float)x - radius), (double)((float)y - radius), (double)((float)z - radius), 
					(double)((float)(x + 1) + radius), (double)((float)(y + 1) + radius), (double)((float)(z + 1) + radius)))) {
				if (player.containerMenu instanceof VaultContainer) {
					IInventory inventory = ((VaultContainer)player.containerMenu).getContents();
					if (inventory == this) {
						++this.openCount;
					}
				}
			}
		}
	}	
	
	/**
	 * 
	 */
	public void updateEntityState() {
		// save the previous positions and angles of vault components
		this.prevLidAngle = this.lidAngle;
		this.prevHandleAngle = this.handleAngle;
		this.prevBoltPosition = this.getBoltPosition();

		// opening ie. players
		if (this.openCount > 0) {
			// test the handle
			if (this.handleAngle > -1.0F) {
				isHandleOpen = false;
				isHandleClosed = false;
				
				this.handleAngle -= 0.1F;
				if (this.handleAngle <= -1.0F) {
					this.handleAngle = -1.0F;
					isHandleOpen = true;
				}	
			} else {
				isHandleOpen = true;
			}
			
			if (this.getBoltPosition() > -2.0F) {
				// NOTE doesn't require isBoltOpen test as it is in sync with handle (isHandleOpen)
				this.setBoltPosition(this.getBoltPosition() - 0.2F);
				if (this.getBoltPosition() <= -2.0F) {
					this.setBoltPosition(-2.0F);
				}
			}
			
			if (isHandleOpen) {
				// play the opening chest sound the at the beginning of opening
				if (this.lidAngle == 0.0F) {
					this.playSound(SoundEvents.CHEST_OPEN);
				}

				// test the lid
				if (this.lidAngle < 1.0F) {
					isLidOpen = false;
					isLidClosed = false;
					this.lidAngle += 0.1F;
					if (this.lidAngle >= 1.0F) {
						this.lidAngle = 1.0F;
						isLidOpen = true;
					}
				} else {
					isLidOpen = true;
				}
			}
		}

		// closing ie no players
		else {
			float f2 = this.lidAngle;

			if (this.lidAngle > 0.0F) {
				isLidClosed = false;
				isLidOpen = false;
				
				this.lidAngle -= 0.1F;
				if (this.lidAngle <= 0.0F) {
					this.lidAngle = 0.0F;
					isLidClosed = true;
				}
			} else {
				isLidClosed = true;
			}

			// play the closing sound
			if (this.lidAngle < 0.06F && f2 >= 0.06F) {
				this.playSound(SoundEvents.CHEST_CLOSE);
			}

			if (isLidClosed) {
				if (this.handleAngle < 0.0F) {
					isHandleClosed = false;
					isHandleOpen = false;
					
					this.handleAngle += 0.1F;
					if (this.handleAngle >= 0.0F) {
						this.handleAngle = 0.0F;
						isHandleClosed = true;
					}
					
					if (this.getBoltPosition() < 0F) {
						this.setBoltPosition(this.getBoltPosition() + 0.2F);
						if (this.getBoltPosition() >= 0F) {
							this.setBoltPosition(0F);
						}
					}
				} else {
					isHandleClosed = true;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param soundIn
	 */
	protected void playSound(SoundEvent soundIn) {
		double d0 = (double)getBlockPos().getX() + 0.5D;
		double d1 = (double)getBlockPos().getY() + 0.5D;
		double d2 = (double)getBlockPos().getZ() + 0.5D;
		level.playSound((PlayerEntity)null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	/**
	 * @return the items
	 */
	public NonNullList<ItemStack> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(NonNullList<ItemStack> chestContents) {
		this.items = chestContents;
	}
	
	@Override
	public ITextComponent getCustomName() {
		return customName;
	}

	/**
	 * @param customName the customName to set
	 */
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
	}

	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("display.vault.name");
	}

	@Override
	public ITextComponent getName() {
		return this.hasCustomName() ? this.getCustomName() : this.getDefaultName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.getName();
	}
	
	@Override
	public float getOpenNess(float partialTicks) {
		return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}

	/**
	 * 
	 */
	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	@Override
	public ItemStack getItem(int index) {
		return getItems().get(index);
	}

	/**
	 * 
	 */
	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack itemStack = ItemStack.EMPTY;

		itemStack = ItemStackHelper.removeItem(this.getItems(), index, count);
		if (!itemStack.isEmpty()) {
			this.setChanged();
		}

		return itemStack;
	}

	/**
	 * 
	 */
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStackHelper.removeItem(this.getItems(), index, 1);
	}

	/**
	 * 
	 */
	@Override
	public void setItem(int index, ItemStack stack) {
		this.getItems().set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.setChanged();
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64,
	 * possibly will be extended.
	 */
	@Override
	public int getMaxStackSize() {
		return Config.GENERAL.stackSize.get();
	}

	/**
	 * 
	 */
	@Override
	public boolean stillValid(PlayerEntity player) {
		if (getLevel().getBlockEntity(this.getBlockPos()) != this) {
			return false;
		} else {
			boolean isUsable = player.distanceToSqr((double) this.getBlockPos().getX() + 0.5D, (double) this.getBlockPos().getY() + 0.5D,
					(double) this.getBlockPos().getZ() + 0.5D) <= 64.0D;
			return isUsable;
		}
	}

	/**
	 * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
	 * clientside.
	 */
	@Override
	public boolean triggerEvent(int id, int count) {
		if (id == 1) {
			this.openCount = count;
			return true;
		} else {
			return super.triggerEvent(id, count);
		}
	}

	/**
	 * 
	 */
	@Override
	public void startOpen(PlayerEntity player) {
		LegacyVault.LOGGER.debug("opening inventory -> {}", player.getName().getString());

		if (!player.isSpectator()) {
			if (this.openCount < 0) {
				this.openCount = 0;
			}
			++this.openCount;
			onOpenOrClose();
		}
	}

	/**
	 * 
	 */
	@Override
	public void stopOpen(PlayerEntity player) {
		LegacyVault.LOGGER.debug("stop Open");
		if (!player.isSpectator()) {
			--this.openCount;			
			// clear the items list
			clearContent();			
			onOpenOrClose();
		}
	}

	protected void onOpenOrClose() {
		Block block = this.getBlockState().getBlock();
		LegacyVault.LOGGER.debug("block for tile entity -> {}, openCount -> {}", block.getClass().getSimpleName(), this.openCount);
		if (block instanceof VaultBlock) {
			getLevel().blockEvent(getBlockPos(), block, 1, this.openCount);
			getLevel().updateNeighborsAt(getBlockPos(), block);
		}
	}
	
	@Override
	public void clearContent() {
		this.getItems().clear();
		this.setChanged();
	}

	//////////// End of IInventory Methods ///////////////

	/////////// Networking //////////////////
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		// TODO write data into the nbttag
		return new SUpdateTileEntityPacket(this.worldPosition, /*3*/ -1, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override 
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		load(null, pkt.getTag());
	}	
	////////////////////////////////////////////////
	
	public Direction getFacing() {
		return facing;
	}

	@Override
	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	@Override
	public void setFacing(int facingIndex) {
		this.facing = Direction.from3DDataValue(facingIndex);
	}
	
	@Override
	public String getOwnerUuid() {
		return ownerUuid;
	}

	@Override
	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public float getHandleAngle() {
		return handleAngle;
	}

	public void setHandleAngle(float handleAngle) {
		this.handleAngle = handleAngle;
	}

	public float getPrevHandleAngle() {
		return prevHandleAngle;
	}

	public void setPrevHandleAngle(float prevHandleAngle) {
		this.prevHandleAngle = prevHandleAngle;
	}

	public boolean isHandleOpen() {
		return isHandleOpen;
	}

	public void setHandleOpen(boolean isHandleOpen) {
		this.isHandleOpen = isHandleOpen;
	}

	public boolean isHandleClosed() {
		return isHandleClosed;
	}

	public void setHandleClosed(boolean isHandleClosed) {
		this.isHandleClosed = isHandleClosed;
	}

	public boolean isLidOpen() {
		return isLidOpen;
	}

	public void setLidOpen(boolean isLidOpen) {
		this.isLidOpen = isLidOpen;
	}

	public boolean isLidClosed() {
		return isLidClosed;
	}

	public void setLidClosed(boolean isLidClosed) {
		this.isLidClosed = isLidClosed;
	}

	public float getLidAngle() {
		return lidAngle;
	}

	public void setLidAngle(float lidAngle) {
		this.lidAngle = lidAngle;
	}

	public float getPrevLidAngle() {
		return prevLidAngle;
	}

	public void setPrevLidAngle(float prevLidAngle) {
		this.prevLidAngle = prevLidAngle;
	}

	public float getBoltPosition() {
		return boltPosition;
	}

	public void setBoltPosition(float boltPosition) {
		this.boltPosition = boltPosition;
	}
}

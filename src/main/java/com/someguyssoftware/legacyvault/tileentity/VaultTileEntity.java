package com.someguyssoftware.legacyvault.tileentity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Nullable;

import com.someguyssoftware.gottschcore.tileentity.AbstractModTileEntity;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.VaultBlock;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.db.entity.Account;
import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;
import com.someguyssoftware.treasure2.Treasure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class VaultTileEntity extends AbstractModTileEntity implements IInventory, IChestLid, ITickableTileEntity, INamedContainerProvider, INameable{

	/*
	 * The FACING index value of the VaultBlock - why is this needed still?
	 */
	private Direction facing;

	private String ownerUuid;

	/*
	 * Vanilla properties for controlling the lid
	 */
	/** The current angle of the lid (between 0 and 1) */
	public float lidAngle;
	/** The angle of the lid last tick */
	public float prevLidAngle;
	/** The number of players currently using this chest */
	public int openCount;
	/** Server sync counter (once per 20 ticks) */
	public int ticksSinceSync;

	/** IInventory properties */
	private NonNullList<ItemStack> items = NonNullList.<ItemStack>withSize(getNumberOfSlots(), ItemStack.EMPTY);
	private int numberOfSlots = 27; // default size
	private ITextComponent customName;

	/**
	 * 
	 */
	public VaultTileEntity() {
		this(LegacyVaultTileEntities.VAULT_TILE_ENTITY_TYPE);
		//		setCustomName(new TranslationTextComponent("display.vault.name"));
	}

	public VaultTileEntity(TileEntityType<?> type) {
		super(type);
		setFacing(Direction.NORTH.get3DDataValue());
	}

	/**
	 * The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the server only
	 * @param windowID
	 * @param playerInventory
	 * @param playerEntity
	 * @return
	 */
	@Nullable
	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return createServerContainer(windowID, playerInventory, playerEntity);
	}

	/**
	 * 
	 * @param windowID
	 * @param inventory
	 * @param player
	 * @return
	 */
	public Container createServerContainer(int windowID, PlayerInventory inventory, PlayerEntity player) {
		return new VaultContainer(windowID, LegacyVaultContainers.STANDARD_VAULT_CONTAINER_TYPE, inventory, this);
	}

	/**
	 * 
	 */
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		try {
			compound.putInt("facing", getFacing().get3DDataValue());
			compound.putString("ownerUuid", getOwnerUuid());
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

	public ITextComponent getCustomName() {
		return customName;
	}

	/**
	 * @param customName the customName to set
	 */
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
	}

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	public void setFacing(int facingIndex) {
		this.facing = Direction.from3DDataValue(facingIndex);
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
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public float getOpenNess(float partialTicks) {
		return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}

	/**
	 * @return the numberOfSlots
	 */
	public int getNumberOfSlots() {
		return 27;
	}

	/**
	 * @param numberOfSlots the numberOfSlots to set
	 */
	public void setNumberOfSlots(int numberOfSlots) {
		this.numberOfSlots = numberOfSlots;
	}

	// TODO most of these can be removed - since not using Locks, don't need to override
	///////////// IInventory Methods ///////////////////////

	/**
	 * 
	 */
	@Override
	public int getContainerSize() {
		return getNumberOfSlots();
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
		return 64;
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
		LegacyVault.LOGGER.info("opening inventory -> {}", player.getName());

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

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot. For guis use Slot.isItemValid
	 */
	//@Override
	//public boolean isItemValidForSlot(int index, ItemStack stack) {
	//return true;
	//}

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

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}
}

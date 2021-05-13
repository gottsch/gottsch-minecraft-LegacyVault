package com.someguyssoftware.legacyvault.tileentity;

import javax.annotation.Nullable;

import com.someguyssoftware.gottschcore.tileentity.AbstractModTileEntity;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.VaultBlock;
import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

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

public class VaultTileEntity extends AbstractModTileEntity implements IInventory, IChestLid, ITickableTileEntity, INamedContainerProvider, INameable{

	/*
	 * The FACING index value of the VaultBlock - why is this needed still?
	 */
	private Direction facing;

	private String ownerUuid;

	public float handleAngle;
	public float prevHandleAngle;
	
	public float boltPosition = 0F;
	public float prevBoltPosition = 0F;

	public boolean isHandleOpen = false;
	public boolean isHandleClosed = true;
	public boolean isBoltOpen = false;
	public boolean isBoltClosed = false;
	public boolean isLidOpen = false;
	public boolean isLidClosed = false;
	
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
		this.prevBoltPosition = this.boltPosition;

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
			
			if (this.boltPosition > -2.0F) {
				// NOTE doesn't require isBoltOpen test as it is in sync with handle (isHandleOpen)
				this.boltPosition -= 0.2F;
				if (this.boltPosition <= -2.0F) {
					this.boltPosition = -2.0F;
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
					
					if (this.boltPosition < 0F) {
						this.boltPosition += 0.2F;
						if (this.boltPosition >= 0F) {
							this.boltPosition = 0F;
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
}

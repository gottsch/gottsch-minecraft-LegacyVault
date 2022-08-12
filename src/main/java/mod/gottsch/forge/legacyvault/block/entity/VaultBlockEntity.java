/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.block.entity;

import javax.annotation.Nullable;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 
 * @author Mark Gottschling on Jun 18, 2022
 *
 */
public class VaultBlockEntity extends BlockEntity implements IVaultBlockEntity {

	private static final String FACING_TAG ="facing";
	private static final String OWNER_UUID_TAG = "ownerUuid";

	/*
	 * The Vault block entity does NOT contain an IItemHandler as it will never hold
	 * any real inventory. The inventory is pulled from the database on per user basis and
	 * the changes in the client container screen do not need to be reflected to the
	 * back end entity.
	 */

	/** The FACING index value of the VaultBlock*/
	private Direction facing;	
	private String ownerUuid;

	/*
	 * Client updated variables
	 */
	/** The current angle of the lid (between 0 and 1) */
	protected float lidAngle;
	/** The angle of the lid last tick */
	protected float prevLidAngle;

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
	 * Server updated properties
	 */
	/** The number of players currently using this chest */
	public int openCount;
	/** Server sync counter (once per 20 ticks) */
	public int ticksSinceSync;

	/**
	 * 
	 * @param pos
	 * @param state
	 */
	public VaultBlockEntity(BlockPos pos, BlockState state) {
		super(Registration.VAULT_BLOCK_ENTITY_TYPE.get(), pos, state);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
	}

	/**
	 * 
	 */
	public void tickClient() {
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
			}
			else {
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
	 * @param sound
	 */
	void playSound(SoundEvent sound) {
		double d0 = (double)getBlockPos().getX() + 0.5D;
		double d1 = (double)getBlockPos().getY() + 0.5D;
		double d2 = (double)getBlockPos().getZ() + 0.5D;
		level.playSound((Player)null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	/**
	 * What happens per tick in this entity on the server
	 */
	public void tickServer() {
		// TODO see VaultTileEntity
		//		updateOpenCount(++this.ticksSinceSync);
		//		updateEntityState();
		//    	LegacyVault.LOGGER.info("ticking from client or server? -> {}", this.level.isClientSide);
	}

	/**
	 * 
	 */
	@Override
	public void load(CompoundTag compound) {		
		try {
			if (compound.contains(FACING_TAG)) {
				this.setFacing(compound.getInt(FACING_TAG));
			}
			if (compound.contains(OWNER_UUID_TAG)) {
				this.setOwnerUuid(compound.getString(OWNER_UUID_TAG));
			}
			super.load(compound);
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error reading to NBT:", e);
		}		
	}

	/**
	 * 
	 */
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		try {
			if (getFacing() != null) {
				compound.putInt(FACING_TAG, getFacing().get3DDataValue());
			}
			if (getOwnerUuid() !=null) {
				compound.putString(OWNER_UUID_TAG, getOwnerUuid());
			}
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error writing to NBT:", e);
		}
	}

	// The getUpdateTag()/handleUpdateTag() pair is called whenever the client receives a new chunk
	// it hasn't seen before. i.e. the chunk is loaded

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		saveAdditional(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		if (tag != null) {
			load(tag);
		}
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		handleUpdateTag(tag);
	}

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	//@Override
	public void setFacing(int facingIndex) {
		this.facing = Direction.from3DDataValue(facingIndex);
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	@Override
	public Component getCustomName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCustomName(Component name) {
		// TODO Auto-generated method stub

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

	public float getBoltPosition() {
		return boltPosition;
	}

	public void setBoltPosition(float boltPosition) {
		this.boltPosition = boltPosition;
	}

	public float getPrevLidAngle() {
		return prevLidAngle;
	}

	public void setPrevLidAngle(float prevLidAngle) {
		this.prevLidAngle = prevLidAngle;
	}

	public float getPrevHandleAngle() {
		return prevHandleAngle;
	}

	public float getHandleAngle() {
		return handleAngle;
	}

	public void setHandleAngle(float handleAngle) {
		this.handleAngle = handleAngle;
	}

	public float getPrevBoltPosition() {
		return prevBoltPosition;
	}

	public void setPrevBoltPosition(float prevBoltPosition) {
		this.prevBoltPosition = prevBoltPosition;
	}

	public void setPrevHandleAngle(float prevHandleAngle) {
		this.prevHandleAngle = prevHandleAngle;
	}

	public float getLidAngle() {
		return lidAngle;
	}

	public void setLidAngle(float lidAngle) {
		this.lidAngle = lidAngle;
	}

}

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
package mod.gottsch.forge.legacyvault.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Mark Gottschling on Feb 18, 2022
 *
 */
public class RusticVaultBlockEntity extends AbstractVaultBlockEntity {

//	private static final String FACING_TAG ="facing";
//	private static final String OWNER_UUID_TAG = "ownerUuid";

	/*
	 * The Vault block entity does NOT contain an IItemHandler as it will never hold
	 * any real inventory. The inventory is pulled from the database on per user basis and
	 * the changes in the client container screen do not need to be reflected to the
	 * back end entity.
	 */

	/*
	 * Client updated variables
	 */
	/** the current angle of the lid (between 0 and 1) */
	public float lidAngle;
	/** the angle of the lid last tick */
	public float prevLidAngle;

	/**
	 * 
	 * @param pos
	 * @param state
	 */
	public RusticVaultBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RUSTIC_VAULT.get(), pos, state);
	}

	public void tickClient() {
		this.prevLidAngle = this.lidAngle;
		if (this.openCount > 0 && this.lidAngle == 0.0F) {
			this.playSound(SoundEvents.CHEST_OPEN);
		}

		if (this.openCount == 0 && this.lidAngle > 0.0F || this.openCount > 0 && this.lidAngle < 1.0F) {
			float f2 = this.lidAngle;

			if (this.openCount > 0) {
				this.lidAngle += 0.1F;
			} else {
				this.lidAngle -= 0.1F;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			//float f3 = 0.5F;
			if (this.lidAngle < 0.5F && f2 >= 0.5F) {
				this.playSound(SoundEvents.CHEST_CLOSE);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

//	/**
//	 *
//	 * @param sound
//	 */
//	void playSound(SoundEvent sound) {
//		double d0 = (double)getBlockPos().getX() + 0.5D;
//		double d1 = (double)getBlockPos().getY() + 0.5D;
//		double d2 = (double)getBlockPos().getZ() + 0.5D;
////		level.playSound(player, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
//        level.playLocalSound(d0, d1, d2, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
//	}
//
//	/**
//	 * What happens per tick in this entity on the server
//	 */
//	public void tickServer() {
//	}
//
//	/**
//	 *
//	 */
//	@Override
//	public void load(CompoundTag compound) {
//		try {
//			if (compound.contains(FACING_TAG)) {
//				this.setFacing(compound.getInt(FACING_TAG));
//			}
//			if (compound.contains(OWNER_UUID_TAG)) {
//				this.setOwnerUuid(compound.getString(OWNER_UUID_TAG));
//			}
//			super.load(compound);
//		} catch (Exception e) {
//			LegacyVault.LOGGER.error("Error reading to NBT:", e);
//		}
//	}
//
//	/**
//	 *
//	 */
//	@Override
//	public void saveAdditional(CompoundTag compound) {
//		super.saveAdditional(compound);
//		try {
//			if (getFacing() != null) {
//				compound.putInt(FACING_TAG, getFacing().get3DDataValue());
//			}
//			if (getOwnerUuid() !=null) {
//				compound.putString(OWNER_UUID_TAG, getOwnerUuid());
//			}
//		} catch (Exception e) {
//			LegacyVault.LOGGER.error("Error writing to NBT:", e);
//		}
//	}
//
//	// The getUpdateTag()/handleUpdateTag() pair is called whenever the client receives a new chunk
//	// it hasn't seen before. i.e. the chunk is loaded
//
//	@Override
//	public CompoundTag getUpdateTag() {
//		CompoundTag tag = super.getUpdateTag();
//		saveAdditional(tag);
//		return tag;
//	}
//
//	@Override
//	public void handleUpdateTag(CompoundTag tag) {
//		if (tag != null) {
//			load(tag);
//		}
//	}
//
//	@Nullable
//	@Override
//	public ClientboundBlockEntityDataPacket getUpdatePacket() {
//		return ClientboundBlockEntityDataPacket.create(this);
//	}
//
//	@Override
//	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//		CompoundTag tag = pkt.getTag();
//		handleUpdateTag(tag);
//	}
//
//	public Direction getFacing() {
//		return facing;
//	}
//
//	public void setFacing(Direction facing) {
//		this.facing = facing;
//	}
//
//	//@Override
//	public void setFacing(int facingIndex) {
//		this.facing = Direction.from3DDataValue(facingIndex);
//	}
//
//	public String getOwnerUuid() {
//		return ownerUuid;
//	}
//
//	public void setOwnerUuid(String ownerUuid) {
//		this.ownerUuid = ownerUuid;
//	}
//
//	@Override
//	public Component getCustomName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setCustomName(Component name) {
//		// TODO Auto-generated method stub
//
//	}

	public float getLidAngle() {
		return lidAngle;
	}

	public void setLidAngle(float lidAngle) {
		this.lidAngle = lidAngle;
	}

}

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
package com.someguyssoftware.legacyvault.block.entity;

import com.someguyssoftware.legacyvault.setup.Registration;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 
 * @author Mark Gottschling on Jun 18, 2022
 *
 */
public class VaultBlockEntity extends BlockEntity {
	private static final String FACING_TAG ="facing";
	private static final String OWNER_UUID_TAG = "ownerUuid";
	
	/*
	 * The Vault block entity does NOT contain an ItemStackHandler as it will never hold
	 * any real inventory. The inventory is pulled from the database on per user basis and
	 * the changes in the client container screen do not need to be reflected to the
	 * back end entity.
	 */
	
	/*
	 * The FACING index value of the VaultBlock - why is this needed still?
	 */
	private Direction facing;
	
	private String ownerUuid;
	
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
        // TODO call capabilities invalidate()
//        handler.invalidate();
//        energy.invalidate();
    }
	
	/**
	 * What happens per tick in this entity
	 */
    public void tickServer() {
    	// TODO see VaultTileEntity
//		updateOpenCount(++this.ticksSinceSync);
//		updateEntityState();
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
			compound.putInt(FACING_TAG, getFacing().get3DDataValue());
			if (getOwnerUuid() !=null) {
				compound.putString(OWNER_UUID_TAG, getOwnerUuid());
			}
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error writing to NBT:", e);
		}
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
}

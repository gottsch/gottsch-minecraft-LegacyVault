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

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Created by Mark Gottschling on 2/18/2025
 */
public class AbstractVaultBlockEntity extends BlockEntity implements IVaultBlockEntity {

    private static final String FACING_TAG ="facing";
    private static final String OWNER_UUID_TAG = "ownerUuid";

    /** The FACING index value of the VaultBlock*/
    private Direction facing;
    private String ownerUuid;

    /*
     * Server updated properties
     */
    /** The number of players currently using this chest */
    public int openCount;
    /** Server sync counter (once per 20 ticks) */
    public int ticksSinceSync;

    /*
     * The Vault block entity does NOT contain an IItemHandler as it will never hold
     * any real inventory. The inventory is pulled from the registry/file system per player and
     * the changes in the client container screen do not need to be reflected to the
     * back end entity.
     */

    public AbstractVaultBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState state) {
        super(entityType, blockPos, state);
    }

    /**
     *
     * @param sound
     */
    void playSound(SoundEvent sound) {
        double d0 = (double)getBlockPos().getX() + 0.5D;
        double d1 = (double)getBlockPos().getY() + 0.5D;
        double d2 = (double)getBlockPos().getZ() + 0.5D;
//		level.playSound(player, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        level.playLocalSound(d0, d1, d2, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
    }

    /**
     * what happens per tick in this entity on the server
     */
    public void tickServer() {
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

    @Override
    public Component getCustomName() {
        // TODO
        return null;
    }

    @Override
    public void setCustomName(Component name) {
        // TODO
    }
}

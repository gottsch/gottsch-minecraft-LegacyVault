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
package com.someguyssoftware.legacyvault.block;

import com.someguyssoftware.gottschcore.block.ModContainerBlock;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IVaultBranchHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.tileentity.IVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * @author Mark Gottschling on May 1, 2021
 *
 */
public class AbstractVaultBlock extends ModContainerBlock implements ILegacyVaultBlock {
	
	private static final VoxelShape MAIN = Block.box(1, 0, 1, 15, 16, 15);
	// TODO latches, feet,  etc.
	
	/*
	 * An array of VoxelShape shapes for the bounding box
	 */
	private VoxelShape[] bounds = new VoxelShape[4];
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public AbstractVaultBlock(String modID, String name, Properties properties) {
		super(modID, name, properties);

		// set the default shapes/shape
		VoxelShape shape = MAIN;
		setBounds(
				new VoxelShape[] {
						shape, 	// N
						shape,  	// E
						shape,  	// S
						shape		// W
				});
	}
	
	/**
	 * 
	 */
	@Override
	   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {

		IVaultTileEntity tileEntity = (IVaultTileEntity) world.getBlockEntity(pos);

		// exit if on the client
		if (WorldInfo.isClientSide(world)) {
			return ActionResultType.SUCCESS;
		}

		// check if a block is blocking the door
		BlockPos blockPos = pos.relative(getFacing(state));
		if (world.getBlockState(blockPos).getMaterial().isSolid() || world.getBlockState(blockPos).getMaterial().isSolidBlocking()) {
			return ActionResultType.SUCCESS;
		}
		
		// check if the vault already has a uuid assigned
		if (!Config.GENERAL.enablePublicVault.get()) {
			LegacyVault.LOGGER.debug("private vault");
			if(tileEntity.getOwnerUuid() == null || tileEntity.getOwnerUuid().isEmpty()) {
				tileEntity.setOwnerUuid(player.getStringUUID());
				LegacyVault.LOGGER.debug("setting vault owner -> {}", player.getStringUUID());
			}
			else if (!tileEntity.getOwnerUuid().equals(player.getStringUUID())) {
				LegacyVault.LOGGER.debug("not your vault!");
				return ActionResultType.SUCCESS;
			}
		}
		
		// get the container provider
		INamedContainerProvider namedContainerProvider = this.getContainer(state, world, pos);
		LegacyVault.LOGGER.debug("namedContainerProvider (TE) -> {}", namedContainerProvider.getClass().getSimpleName());
		// open the chest
		NetworkHooks.openGui((ServerPlayerEntity)player, namedContainerProvider, (packetBuffer)->{});
		// NOTE: (packetBuffer)->{} is just a do-nothing because we have no extra data to send

		return ActionResultType.SUCCESS;
	}
	
	/**
	 * Called just after the player places a block.
	 */
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		LegacyVault.LOGGER.debug("setPlacedBy() - Placing chest from item");

		VaultTileEntity valutTileEntity = null;

		// face the block towards the player (there isn't really a front)
		worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity != null && tileEntity instanceof VaultTileEntity) {
			// get the backing tile entity
			valutTileEntity = (VaultTileEntity) tileEntity;

			// set the name of the chest
			if (stack.hasCustomHoverName()) {
				valutTileEntity.setCustomName(stack.getDisplayName());
			}

			// update the facing
			valutTileEntity.setFacing(placer.getDirection().getOpposite());
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state,
			TileEntity tileEntity, ItemStack itemStack) {

		// get  player capabilities
		IVaultBranchHandler cap = player.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
			return new RuntimeException("player does not have VaultBranchHandler capability.'");
		});
		
		// decrement cap vault branch count
		if (cap.getCount() > 0) {
			// TODO increment capability size (rename to count)
			int count = cap.getCount() - 1;
			count = count < 0 ? 0 : count;
			cap.setCount(count);
		}
		
		super.playerDestroy(world, player, pos, state, tileEntity, itemStack);
	}
	
	/**
	 * Convenience method.
	 * @param state
	 * @return
	 */
	public static Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}
	
	/**
	 * 
	 */
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(state.getValue(FACING)) {
		default:
		case NORTH:
			return bounds[0];
		case EAST:
			return bounds[1];
		case SOUTH:
			return bounds[2];
		case WEST:
			return bounds[3];
		}
	}
	
	@Override
	public VoxelShape[] getBounds() {
		return bounds;
	}

	@Override
	public ILegacyVaultBlock setBounds(VoxelShape[] bounds) {
		this.bounds = bounds;
		return this;
	}
}

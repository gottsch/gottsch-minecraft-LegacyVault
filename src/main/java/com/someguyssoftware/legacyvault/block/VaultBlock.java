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

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.inventory.VaultSlotSize;
import com.someguyssoftware.legacyvault.tileentity.LargeVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.MediumVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class VaultBlock extends AbstractVaultBlock  implements ILegacyVaultBlock {
	private static final VoxelShape NORTH_MAIN = Block.box(1, 1, 2, 15, 16, 15);
	private static final VoxelShape NORTH_FOOT1 = Block.box(1, 0, 2, 3, 1, 4);
	private static final VoxelShape NORTH_FOOT2 = Block.box(13, 0, 2, 15, 1, 4);
	private static final VoxelShape NORTH_FOOT3 = Block.box(1, 0, 13, 3, 1, 15);
	private static final VoxelShape NORTH_FOOT4 = Block.box(13, 0, 13, 15, 1, 15);
	
	private static final VoxelShape SOUTH_MAIN = Block.box(1, 1, 1, 15, 16, 14);
	private static final VoxelShape SOUTH_FOOT1 = Block.box(1, 0, 1, 3, 1, 3);
	private static final VoxelShape SOUTH_FOOT2 = Block.box(13, 0, 1, 15, 1, 3);
	private static final VoxelShape SOUTH_FOOT3 = Block.box(1, 0, 12, 3, 1, 14);
	private static final VoxelShape SOUTH_FOOT4 = Block.box(13, 0, 12, 15, 1, 14);
	
	private static final VoxelShape EAST_MAIN = Block.box(1, 1, 1, 14, 16, 15);
	private static final VoxelShape EAST_FOOT1 = Block.box(1, 0, 13, 3, 1, 15);
	private static final VoxelShape EAST_FOOT2 = Block.box(12, 0, 13, 14, 1, 15);
	private static final VoxelShape EAST_FOOT3 = Block.box(1, 0, 1, 3, 1, 3);
	private static final VoxelShape EAST_FOOT4 = Block.box(12, 0, 1, 14, 1, 3);
	
	private static final VoxelShape WEST_MAIN = Block.box(2, 1, 1, 15, 16, 15);
	private static final VoxelShape WEST_FOOT1 = Block.box(13, 0, 1, 15, 1, 3);
	private static final VoxelShape WEST_FOOT2 = Block.box(2, 0, 1, 4, 1, 3);
	private static final VoxelShape WEST_FOOT3 = Block.box(13, 0, 13, 15, 1, 15);
	private static final VoxelShape WEST_FOOT4 = Block.box(2, 0, 13, 4, 1, 15);
	
	private static final VoxelShape NORTH_VAULT = VoxelShapes.or(NORTH_MAIN, NORTH_FOOT1, NORTH_FOOT2, NORTH_FOOT3, NORTH_FOOT4);	
	private static final VoxelShape SOUTH_VAULT = VoxelShapes.or(SOUTH_MAIN, SOUTH_FOOT1, SOUTH_FOOT2, SOUTH_FOOT3, SOUTH_FOOT4);
	private static final VoxelShape EAST_VAULT = VoxelShapes.or(EAST_MAIN, EAST_FOOT1, EAST_FOOT2, EAST_FOOT3, EAST_FOOT4);
	private static final VoxelShape WEST_VAULT = VoxelShapes.or(WEST_MAIN, WEST_FOOT1, WEST_FOOT2, WEST_FOOT3, WEST_FOOT4);
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public VaultBlock(String modID, String name, Properties properties) {
		super(modID, name, properties);

		setBounds(
				new VoxelShape[] {
						NORTH_VAULT, 	// N
						EAST_VAULT,  	// E
						SOUTH_VAULT,  	// S
						WEST_VAULT		// W
				});
	}
	
	/**
	 * 
	 */
	@Override
	 public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		TileEntity vaultTileEntity = null;
		try {
			int size = Config.GENERAL.inventorySize.get();
			if (size <=VaultSlotSize.SMALL.getSize()) {
				vaultTileEntity = new VaultTileEntity();
			}
			else if (size <= VaultSlotSize.MEDIUM.getSize()) {
				vaultTileEntity = new MediumVaultTileEntity();
			}
			else {
				vaultTileEntity = new LargeVaultTileEntity();
			}
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error(e);
		}
		LegacyVault.LOGGER.debug("created tile entity -> {}", vaultTileEntity.getClass().getSimpleName());
		return vaultTileEntity;
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	/**
	 * 
	 */
	@Override
	   public BlockRenderType getRenderShape(BlockState state) {
	      return BlockRenderType.ENTITYBLOCK_ANIMATED;
	   }
	
	/**
	 * 
	 */
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockState = this.defaultBlockState().setValue(FACING,
				context.getHorizontalDirection().getOpposite());
		return blockState;
	}
	
}

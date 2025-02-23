/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.block;

import javax.annotation.Nullable;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.entity.RusticVaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


/**
 * @author Mark Gottschling on Feb 18, 2025
 *
 */
public class RusticVaultBlock extends PersonalVaultBlock {
	private static final VoxelShape MAIN = Block.box(0.1, 0, 0.1, 15.9, 9, 15.9);
	private static final VoxelShape Z_AXIS_TOP = Block.box(0, 9, 4.5, 15.9, 15, 11.5);
	private static final VoxelShape X_AXIS_TOP = Block.box(4.5, 9, 0.1, 11.5, 15, 15.9);

	private static final VoxelShape Z_AXIS_SHAPE = Shapes.or(MAIN, Z_AXIS_TOP);
	private static final VoxelShape X_AXIS_SHAPE = Shapes.or(MAIN, X_AXIS_TOP);

	/**
	 *
	 * @param properties
	 */
	public RusticVaultBlock(Properties properties) {
		super(properties);

		setBounds(
				new VoxelShape[] {
						Z_AXIS_SHAPE, 	// N
						X_AXIS_SHAPE,  // E
						Z_AXIS_SHAPE,  // S
						X_AXIS_SHAPE   // W
				});
	}

	/**
	 *
	 */
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		BlockEntity vaultBlockEntity = null;
		try {
			vaultBlockEntity = new RusticVaultBlockEntity(pos, state);
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error(e);
		}
		LegacyVault.LOGGER.debug("created block entity -> {}", vaultBlockEntity.getClass().getSimpleName());
		return vaultBlockEntity;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return (lvl, pos, blockState, t) -> {
				if (t instanceof RusticVaultBlockEntity entity) { // test and cast
					entity.tickClient();
				}
			};
		}
		return null;
	}
}

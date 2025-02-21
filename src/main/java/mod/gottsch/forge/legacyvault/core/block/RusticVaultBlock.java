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

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.block.entity.RusticVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author Mark Gottschling on Feb 18, 2025
 *
 */
public class RusticVaultBlock extends PersonalVaultBlock {
	private static final VoxelShape NORTH_MAIN = Block.box(0, 0, 0, 15.9, 15.9, 15.9);
//	private static final VoxelShape NORTH_FOOT1 = Block.box(0.5, 0, 0.5, 2.5, 1, 2.5);
//	private static final VoxelShape NORTH_FOOT2 = Block.box(13, 0, 2, 15, 1, 4);
//	private static final VoxelShape NORTH_FOOT3 = Block.box(1, 0, 13, 3, 1, 15);
//	private static final VoxelShape NORTH_FOOT4 = Block.box(13, 0, 13, 15, 1, 15);

	private static final VoxelShape SOUTH_MAIN = Block.box(0, 0, 0, 15.9, 15.9, 15.9);
//	private static final VoxelShape SOUTH_FOOT1 = Block.box(1, 0, 1, 3, 1, 3);
//	private static final VoxelShape SOUTH_FOOT2 = Block.box(13, 0, 1, 15, 1, 3);
//	private static final VoxelShape SOUTH_FOOT3 = Block.box(1, 0, 12, 3, 1, 14);
//	private static final VoxelShape SOUTH_FOOT4 = Block.box(13, 0, 12, 15, 1, 14);

	private static final VoxelShape EAST_MAIN = Block.box(0, 0, 0, 15.9, 15.9, 15.9);
//	private static final VoxelShape EAST_FOOT1 = Block.box(1, 0, 13, 3, 1, 15);
//	private static final VoxelShape EAST_FOOT2 = Block.box(12, 0, 13, 14, 1, 15);
//	private static final VoxelShape EAST_FOOT3 = Block.box(1, 0, 1, 3, 1, 3);
//	private static final VoxelShape EAST_FOOT4 = Block.box(12, 0, 1, 14, 1, 3);

	private static final VoxelShape WEST_MAIN = Block.box(0, 0, 0, 15.9, 15.9, 15.9);
//	private static final VoxelShape WEST_FOOT1 = Block.box(13, 0, 1, 15, 1, 3);
//	private static final VoxelShape WEST_FOOT2 = Block.box(2, 0, 1, 4, 1, 3);
//	private static final VoxelShape WEST_FOOT3 = Block.box(13, 0, 13, 15, 1, 15);
//	private static final VoxelShape WEST_FOOT4 = Block.box(2, 0, 13, 4, 1, 15);

	private static final VoxelShape NORTH_VAULT = Shapes.or(NORTH_MAIN);//, NORTH_FOOT1, NORTH_FOOT2, NORTH_FOOT3, NORTH_FOOT4);
	private static final VoxelShape SOUTH_VAULT = Shapes.or(SOUTH_MAIN);//, SOUTH_FOOT1, SOUTH_FOOT2, SOUTH_FOOT3, SOUTH_FOOT4);
	private static final VoxelShape EAST_VAULT = Shapes.or(EAST_MAIN);//, EAST_FOOT1, EAST_FOOT2, EAST_FOOT3, EAST_FOOT4);
	private static final VoxelShape WEST_VAULT = Shapes.or(WEST_MAIN);//, WEST_FOOT1, WEST_FOOT2, WEST_FOOT3, WEST_FOOT4);

	/**
	 * 
	 * @param properties
	 */
	public RusticVaultBlock(Properties properties) {
		super(properties);
		
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

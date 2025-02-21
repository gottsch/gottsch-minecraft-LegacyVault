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

import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.entity.AbstractVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.block.entity.CommunityVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.block.entity.RusticVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.inventory.CommunityVaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Created by Mark Gottschling on 2/20/2025
 */
public class CommunityVaultBlock extends AbstractVaultBlock  implements ILegacyVaultBlock {

    private static final VoxelShape VAULT = Block.box(0, 0, 0, 15.9, 15.9, 15.9);

    /**
     * @param properties
     */
    public CommunityVaultBlock(Properties properties) {
        super(properties);
        setBounds(
                new VoxelShape[] {
                        VAULT, 	// N
                        VAULT,  	// E
                        VAULT,  	// S
                        VAULT		// W
                });
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        BlockEntity vaultBlockEntity = null;
        try {
            vaultBlockEntity = new CommunityVaultBlockEntity(pos, state);
        }
        catch(Exception e) {
            LegacyVault.LOGGER.error(e);
        }
        return vaultBlockEntity;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof CommunityVaultBlockEntity entity) { // test and cast
                    entity.tickClient();
                }
            };
        }
        return null;
    }

    @Override
    public boolean doesPlayerHaveAccess(AbstractVaultBlockEntity blockEntity, Player player) {
        if (Config.ServerConfig.COMMUNITY.communityVault.get()) {
            return ModUtil.doesPlayerHaveCommunityAccess(player);
        }
        return false;
    }

    @Override
    public MenuProvider getMenuProvider(BlockPos pos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("display.vault.name");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                return new CommunityVaultContainerMenu(windowId, pos, playerInventory, playerEntity);
            }
        };
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter blockReader, BlockPos blockPos) {
        if (!player.isCreative()) {
            // prevent player from destroying vault if they don't have access
            if ((Config.ServerConfig.COMMUNITY.communityVault.get())) {
                return 0;
            }
        }
        return super.getDestroyProgress(state, player, blockReader, blockPos);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack itemStack) {

        if (WorldInfo.isClientSide(world)) {
            return;
        }

        LegacyVault.LOGGER.debug("player is destroying vault block");
        if (!player.isCreative()) {
            if (Config.ServerConfig.COMMUNITY.communityVault.get()) {
                return;
            }
        }
        super.playerDestroy(world, player, pos, state, blockEntity, itemStack);
    }
}

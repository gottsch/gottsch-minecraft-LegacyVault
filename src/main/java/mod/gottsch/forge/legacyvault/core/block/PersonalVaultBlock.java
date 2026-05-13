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

import mod.gottsch.forge.gottschcore.spatial.DimensionCoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.entity.AbstractVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.inventory.PersonalVaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * @author by Mark Gottschling on 2/21/2025
 */
public abstract class PersonalVaultBlock extends AbstractVaultBlock implements ILegacyVaultBlock {
    /**
     * @param properties
     */
    public PersonalVaultBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult result) {
        if (hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).is(ModItems.VAULT_UPGRADE.get())) {
            if (WorldInfo.isClientSide(world)) {
                return InteractionResult.SUCCESS;
            }
            AbstractVaultBlockEntity blockEntity = (AbstractVaultBlockEntity) world.getBlockEntity(pos);
            if (!doesPlayerHaveAccess(blockEntity, player)) {
                return InteractionResult.SUCCESS;
            }
            return applyUpgrade(player, player.getItemInHand(hand));
        }
        return super.use(state, world, pos, player, hand, result);
    }

    private InteractionResult applyUpgrade(Player player, ItemStack upgradeItem) {
        IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElse(null);
        if (cap == null) {
            LegacyVault.LOGGER.warn("applyUpgrade: player {} missing vault capability", player.getScoreboardName());
            return InteractionResult.FAIL;
        }
        int maxTier = Config.ServerConfig.PERSONAL.maxTier.get();
        if (cap.getVaultTier() >= maxTier) {
            player.displayClientMessage(
                    Component.translatable(LangUtil.screen("vault.upgrade.maxed")), true);
            return InteractionResult.SUCCESS;
        }
        cap.setVaultTier(cap.getVaultTier() + 1);
        if (!player.isCreative()) {
            upgradeItem.shrink(1);
        }
        // persist immediately so a crash doesn't lose the upgrade (capability is session-only,
        // the vault file is the cross-world source of truth)
        VaultPersistenceManager.save(player);
        player.displayClientMessage(
                Component.translatable(LangUtil.screen("vault.upgrade.success"), cap.getVaultTier(), maxTier), true);
        LegacyVault.LOGGER.debug("vault upgrade: player {} now at tier {}/{}", player.getScoreboardName(), cap.getVaultTier(), maxTier);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean doesPlayerHaveAccess(AbstractVaultBlockEntity blockEntity, Player player) {
        if (Config.ServerConfig.PERSONAL.enabled.get()) {
            return (blockEntity.getOwnerUuid() != null && blockEntity.getOwnerUuid().equals(player.getStringUUID()));
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
                return new PersonalVaultContainerMenu(windowId, pos, playerInventory, playerEntity);
            }
        };
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        // face the block towards the player (there isn't really a front)
        worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof IVaultBlockEntity vaultBlockEntity) {
            // set the owner of the chest
            if (Config.ServerConfig.PERSONAL.enabled.get()) {
                vaultBlockEntity.setOwnerUuid(placer.getStringUUID());
                if (LegacyVault.LOGGER.isDebugEnabled()) {
                    LegacyVault.LOGGER.debug("setting vault owner -> {}", placer.getStringUUID());
                }
            }
        }

    }

    /**
     * Fires for all block removals — survival, creative, commands, explosions.
     * Cap cleanup lives here so it is never skipped regardless of how the vault is removed.
     */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !WorldInfo.isClientSide(level)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof IVaultBlockEntity vaultBlockEntity) {
                String ownerUuid = vaultBlockEntity.getOwnerUuid();
                if (ownerUuid != null && !ownerUuid.isEmpty()) {
                    try {
                        Player owner = level.getPlayerByUUID(UUID.fromString(ownerUuid));
                        if (owner != null) {
                            IPlayerVaultsHandler cap = owner.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElse(null);
                            if (cap != null) {
                                DimensionCoords vaultLocation = DimensionCoords.of(level.dimension(), pos);
                                List<DimensionCoords> newLocations = new ArrayList<>();
                                for (DimensionCoords location : cap.getLocations()) {
                                    if (!location.equals(vaultLocation)) {
                                        newLocations.add(location);
                                    }
                                }
                                cap.setLocations(newLocations);

                                if (!Config.ServerConfig.PERSONAL.unlimitedVaults.get()) {
                                    int count = Math.max(cap.getCount() - 1, 0);
                                    cap.setCount(count);
                                    if (owner instanceof ServerPlayer serverPlayer) {
                                        VaultCountMessageToClient message = new VaultCountMessageToClient(serverPlayer.getStringUUID(), count);
                                        LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
                                    }
                                }
                            } else {
                                LegacyVault.LOGGER.warn("onRemove: vault owner {} is missing PlayerVaultsHandler capability", ownerUuid);
                            }
                        } else {
                            LegacyVault.LOGGER.warn("onRemove: vault owner {} is offline; location/count not updated", ownerUuid);
                        }
                    } catch (Exception e) {
                        LegacyVault.LOGGER.error("onRemove: unable to get player by uuid -> " + ownerUuid, e);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack itemStack) {
        LegacyVault.LOGGER.debug("player is destroying vault block");
        super.playerDestroy(world, player, pos, state, blockEntity, itemStack);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter blockReader, BlockPos blockPos) {
//        if (!player.isCreative()) {
//            // prevent player from destroying vault if they don't have access
//            if ((Config.ServerConfig.COMMUNITY.enabled.get())) {
//                return 0;
//            }
//        }
        return super.getDestroyProgress(state, player, blockReader, blockPos);
    }
}

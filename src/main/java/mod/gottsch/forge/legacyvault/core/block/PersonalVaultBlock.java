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

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.entity.AbstractVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.inventory.PersonalVaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
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
                return new TranslatableComponent("display.vault.name");
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
     * NOTE this is called only in survival!
     */
    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack itemStack) {

        LegacyVault.LOGGER.debug("player is destroying vault block");
        if (WorldInfo.isClientSide(world)) {
            return;
        }

        // get the vault-owning player (not the current player who is destroying block)
        if (blockEntity instanceof IVaultBlockEntity vaultBlockEntity) {

            // get the owner by uuid
            String vaultOwnerPlayerUUID = vaultBlockEntity.getOwnerUuid();
            Player vaultOwnerPlayer = null;
            if (vaultOwnerPlayerUUID != null && !vaultOwnerPlayerUUID.isEmpty()) {
                try {
                    vaultOwnerPlayer = world.getPlayerByUUID(UUID.fromString(vaultOwnerPlayerUUID));
                }
                catch(Exception e) {
                    LegacyVault.LOGGER.error("unable to get player by uuid -> " + vaultOwnerPlayerUUID, e);
                }
            }

            if (vaultOwnerPlayer != null) {
                // get  player capabilities
                IPlayerVaultsHandler cap = vaultOwnerPlayer.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
                    return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
                });
                LegacyVault.LOGGER.debug("player vault count -> {}", cap.getCount());

                if (!Config.ServerConfig.PERSONAL.unlimitedVaults.get()) {
                    // decrement cap vault branch count
                    if (cap.getCount() > 0) {
                        // decrement count
                        int count = cap.getCount() - 1;
                        count = Math.max(count, 0);
                        cap.setCount(count);

                        LegacyVault.LOGGER.debug("player vault branch count -> {}", cap.getCount());
                        // send state message to client
                        VaultCountMessageToClient message = new VaultCountMessageToClient(vaultOwnerPlayerUUID, count);
                        ServerPlayer serverPlayer = (ServerPlayer)vaultOwnerPlayer;
                        LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),message);
                    }
                }

                // remove location
                ICoords vaultLocation = new Coords(pos);
                List<ICoords> newLocations = new ArrayList<>();
                for (ICoords location : cap.getLocations()) {
                    if (!location.equals(vaultLocation)) {
                        newLocations.add(location);
                    }
                }
                cap.setLocations(newLocations);
            }
        }
        super.playerDestroy(world, player, pos, state, blockEntity, itemStack);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter blockReader, BlockPos blockPos) {
        if (!player.isCreative()) {
            // prevent player from destroying vault if they don't have access
            if ((Config.ServerConfig.COMMUNITY.enabled.get())) {
                return 0;
            }
        }
        return super.getDestroyProgress(state, player, blockReader, blockPos);
    }
}

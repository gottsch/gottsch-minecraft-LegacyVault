/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.block.entity.VaultBlockEntity;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.network.VaultCountMessageToClient;
import mod.gottsch.forge.legacyvault.util.LegacyVaultHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;



/**
 * @author Mark Gottschling on May 1, 2021
 *
 */
public abstract class AbstractVaultBlock extends BaseEntityBlock implements ILegacyVaultBlock {
	/*
	 * An array of VoxelShape shapes for the bounding box
	 */
	private VoxelShape[] bounds = new VoxelShape[4];

	/**
	 * 
	 * @param properties
	 */
	public AbstractVaultBlock(Properties properties) {
		super(properties);
	}

	/**
	 * 
	 */
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
		InteractionHand hand, BlockHitResult result) {

		LegacyVault.LOGGER.debug("using vault...");
		VaultBlockEntity blockEntity = (VaultBlockEntity) world.getBlockEntity(pos);

		if (WorldInfo.isClientSide(world)) {
			return InteractionResult.SUCCESS;
		}

		// check if a block is blocking the door
		BlockPos blockPos = pos.relative(getFacing(state));
		if (!world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos).canBeReplaced()) {
			return InteractionResult.SUCCESS;
		}

		// check if the vault already has a uuid assigned
		if (!ServerConfig.PUBLIC_VAULT.enablePublicVault.get()) {
			LegacyVault.LOGGER.debug("private vault");

			if (blockEntity.getOwnerUuid() != null && !blockEntity.getOwnerUuid().equals(player.getStringUUID())) {
				LegacyVault.LOGGER.debug("not your vault!");
				return InteractionResult.SUCCESS;
			}
		}
		else if (!LegacyVaultHelper.doesPlayerHavePulicAccess(player)) {
			LegacyVault.LOGGER.debug("player does not have access!");
			return InteractionResult.SUCCESS;
		}

		// get the container provider
		MenuProvider containerProvider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("display.vault.name");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                return new VaultContainerMenu(windowId, pos, playerInventory, playerEntity);
            }
        };
        NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());

		return InteractionResult.SUCCESS;
	}

	/**
	 * Called just after the player places a block.
	 * Sets the owner of the vault and other TE related data.
	 */
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		LegacyVault.LOGGER.debug("setPlacedBy() - placing vault from item");

		IVaultBlockEntity vaultBlockEntity = null;

		// face the block towards the player (there isn't really a front)
		worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (blockEntity != null && blockEntity instanceof IVaultBlockEntity) {
			vaultBlockEntity = (IVaultBlockEntity) blockEntity;
			LegacyVault.LOGGER.debug("public vault -> {}", ServerConfig.PUBLIC_VAULT.enablePublicVault.get());
			LegacyVault.LOGGER.debug("placer uuid -> {}", placer.getStringUUID());
			// set the owner of the chest
			if (!ServerConfig.PUBLIC_VAULT.enablePublicVault.get()) {
				vaultBlockEntity.setOwnerUuid(placer.getStringUUID());
				LegacyVault.LOGGER.debug("setting vault owner -> {}", placer.getStringUUID());
			}

			// set the name of the chest
			if (stack.hasCustomHoverName()) {
				vaultBlockEntity.setCustomName(stack.getDisplayName());
			}

			// update the facing
			vaultBlockEntity.setFacing(placer.getDirection().getOpposite());
		}
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		super.destroy(world, pos, state);
	}

	/**
	 * 
	 */
	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter blockReader, BlockPos blockPos) {

		// prevent player from destroying vault if they don't have access
		if((ServerConfig.PUBLIC_VAULT.enablePublicVault.get())) {
			return 0;
		}

		return super.getDestroyProgress(state, player, blockReader, blockPos);
	}

	/**
	 * NOTE this is called only in survival!
	 */
	@Override
	public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,
			BlockEntity blockEntity, ItemStack itemStack) {

		LegacyVault.LOGGER.debug("player is destroying vault block");
		if (WorldInfo.isClientSide(world) || ServerConfig.PUBLIC_VAULT.enablePublicVault.get()) {
			return;
		}

		// get the vault-owning player (not the current player who is destroying block)
		if (blockEntity != null && blockEntity instanceof IVaultBlockEntity) {
			IVaultBlockEntity vaultBlockEntity = (IVaultBlockEntity) blockEntity;

			// get the owner by uuid
			String playerUUID = vaultBlockEntity.getOwnerUuid();
			Player vaultOwnerPlayer = null;
			if (playerUUID != null && !playerUUID.isEmpty()) {
				try {
					vaultOwnerPlayer = world.getPlayerByUUID(UUID.fromString(playerUUID));
				}
				catch(Exception e) {
					LegacyVault.LOGGER.error("unable to get player by uuid -> " + playerUUID, e);
				}
			}

			if (vaultOwnerPlayer != null) {
				// get  player capabilities
				IPlayerVaultsHandler cap = vaultOwnerPlayer.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
				});
				LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());

				if (ServerConfig.GENERAL.enableLimitedVaults.get()) {
					// decrement cap vault branch count
					if (cap.getCount() > 0) {
						// decrement count
						int count = cap.getCount() - 1;
						count = count < 0 ? 0 : count;
						cap.setCount(count);

						LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
						// send state message to client
						VaultCountMessageToClient message = new VaultCountMessageToClient(playerUUID, count);
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
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.someguyssoftware.gottschcore.block.ModContainerBlock;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IPlayerVaultsHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.init.LegacyVaultSetup;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;
import com.someguyssoftware.legacyvault.network.VaultCountMessageToClient;
import com.someguyssoftware.legacyvault.tileentity.IVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;
import com.someguyssoftware.legacyvault.util.LegacyVaultHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Mark Gottschling on May 1, 2021
 *
 */
public class AbstractVaultBlock extends ModContainerBlock implements ILegacyVaultBlock {

	private static final VoxelShape MAIN = Block.box(1, 0, 1, 15, 16, 15);

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
		LegacyVault.LOGGER.debug("using vault...");
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
		if (!Config.PUBLIC_VAULT.enablePublicVault.get()) {
			LegacyVault.LOGGER.debug("private vault");

			if (tileEntity.getOwnerUuid() != null && !tileEntity.getOwnerUuid().equals(player.getStringUUID())) {
				LegacyVault.LOGGER.debug("not your vault!");
				return ActionResultType.SUCCESS;
			}
		}
		else if (!LegacyVaultHelper.doesPlayerHavePulicAccess(player)) {
			LegacyVault.LOGGER.debug("player does not have access!");
			return ActionResultType.SUCCESS;
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
	 * Sets the owner of the vault and other TE related data.
	 */
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		LegacyVault.LOGGER.debug("setPlacedBy() - Placing chest from item");

		IVaultTileEntity vaultTileEntity = null;

		// face the block towards the player (there isn't really a front)
		worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity != null && tileEntity instanceof IVaultTileEntity) {
			// get the backing tile entity
			vaultTileEntity = (IVaultTileEntity) tileEntity;
			LegacyVault.LOGGER.debug("got the tile entity.");
			LegacyVault.LOGGER.debug("public vault -> {}", Config.PUBLIC_VAULT.enablePublicVault.get());
			LegacyVault.LOGGER.debug("placer uuid -> {}", placer.getStringUUID());
			// set the owner of the chest
			if (!Config.PUBLIC_VAULT.enablePublicVault.get()) {
				vaultTileEntity.setOwnerUuid(placer.getStringUUID());
				LegacyVault.LOGGER.debug("setting vault owner -> {}", placer.getStringUUID());
			}

			// set the name of the chest
			if (stack.hasCustomHoverName()) {
				vaultTileEntity.setCustomName(stack.getDisplayName());
			}

			// update the facing
			vaultTileEntity.setFacing(placer.getDirection().getOpposite());
		}
	}

	@Override
	public void destroy(IWorld world, BlockPos pos, BlockState state) {
		//		LegacyVault.LOGGER.debug("calling destroy()");
		super.destroy(world, pos, state);
	}

	/**
	 * 
	 */
	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader blockReader, BlockPos blockPos) {

		// prevent player from destroying vault if they don't have access
		if((Config.PUBLIC_VAULT.enablePublicVault.get())) {
			return 0;
		}

		return super.getDestroyProgress(state, player, blockReader, blockPos);
	}

	/**
	 * NOTE this is called only in survival!
	 */
	@Override
	public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state,
			TileEntity tileEntity, ItemStack itemStack) {

		LegacyVault.LOGGER.debug("player is destroying vault block");
		if (WorldInfo.isClientSide(world) || Config.PUBLIC_VAULT.enablePublicVault.get()) {
			return;
		}

		// get the vault-owning player (not the current player who is destroying block)
		if (tileEntity != null && tileEntity instanceof IVaultTileEntity) {
			// get the backing tile entity
			IVaultTileEntity vaultTileEntity = (IVaultTileEntity) tileEntity;

			// get the owner by uuid
			String playerUUID = vaultTileEntity.getOwnerUuid();
			PlayerEntity vaultOwnerPlayer = null;
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
				IPlayerVaultsHandler cap = vaultOwnerPlayer.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
				});
				LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());

				if (Config.GENERAL.enableLimitedVaults.get()) {
					// decrement cap vault branch count
					if (cap.getCount() > 0) {
						// decrement count
						int count = cap.getCount() - 1;
						count = count < 0 ? 0 : count;
						cap.setCount(count);

						LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
						// send state message to client
						VaultCountMessageToClient message = new VaultCountMessageToClient(playerUUID, count);
						ServerPlayerEntity serverPlayer = (ServerPlayerEntity)vaultOwnerPlayer;
						LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),message);
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

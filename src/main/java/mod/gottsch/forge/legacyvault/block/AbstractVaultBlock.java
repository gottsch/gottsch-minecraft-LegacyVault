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
package mod.gottsch.forge.legacyvault.block;

import java.util.UUID;

import com.someguyssoftware.gottschcore.block.ModContainerBlock;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.inventory.VaultContainerMenu;
import com.someguyssoftware.legacyvault.util.LegacyVaultHelper;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.entity.IVaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;



/**
 * @author Mark Gottschling on May 1, 2021
 *
 */
public abstract class AbstractVaultBlock extends ModContainerBlock implements ILegacyVaultBlock {

	private static final VoxelShape MAIN = Block.box(1, 0, 1, 15, 16, 15);

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
	 * @param modID
	 * @param name
	 * @param properties
	 */
	@Deprecated
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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
		InteractionHand hand, BlockHitResult result) {

		LegacyVault.LOGGER.debug("using vault...");
//		TODO reinstate and modify
//		IVaultBlockEntity tileEntity = (IVaultBlockEntity) world.getBlockEntity(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		// exit if on the client
		if (WorldInfo.isClientSide(world)) {
			return InteractionResult.SUCCESS;
		}

		// check if a block is blocking the door
		BlockPos blockPos = pos.relative(getFacing(state));
		if (world.getBlockState(blockPos).getMaterial().isSolid() || world.getBlockState(blockPos).getMaterial().isSolidBlocking()) {
			return InteractionResult.SUCCESS;
		}

		// check if the vault already has a uuid assigned
		if (!Config.PUBLIC_VAULT.enablePublicVault.get()) {
			LegacyVault.LOGGER.debug("private vault");

			// TODO reinstate
//			if (blockEntity.getOwnerUuid() != null && !blockEntity.getOwnerUuid().equals(player.getStringUUID())) {
//				LegacyVault.LOGGER.debug("not your vault!");
//				return InteractionResult.SUCCESS;
//			}
		}
		else if (!LegacyVaultHelper.doesPlayerHavePulicAccess(player)) {
			LegacyVault.LOGGER.debug("player does not have access!");
			return InteractionResult.SUCCESS;
		}

		// get the container provider
		MenuProvider containerProvider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TranslatableComponent(""); // TODO
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                return new VaultContainerMenu(windowId, pos, playerInventory, playerEntity);
            }
        };
        NetworkHooks.openGui((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());

		return InteractionResult.SUCCESS;
	}

	/**
	 * Called just after the player places a block.
	 * Sets the owner of the vault and other TE related data.
	 */
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		LegacyVault.LOGGER.debug("setPlacedBy() - Placing chest from item");

		IVaultBlockEntity vaultTileEntity = null;

		// face the block towards the player (there isn't really a front)
		worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity != null && tileEntity instanceof IVaultBlockEntity) {
			// get the backing tile entity
			vaultTileEntity = (IVaultBlockEntity) tileEntity;
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
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		//		LegacyVault.LOGGER.debug("calling destroy()");
		super.destroy(world, pos, state);
	}

	/**
	 * 
	 */
	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter blockReader, BlockPos blockPos) {

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
	public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,
			BlockEntity tileEntity, ItemStack itemStack) {

		LegacyVault.LOGGER.debug("player is destroying vault block");
		if (WorldInfo.isClientSide(world) || Config.PUBLIC_VAULT.enablePublicVault.get()) {
			return;
		}

		// get the vault-owning player (not the current player who is destroying block)
		if (tileEntity != null && tileEntity instanceof IVaultBlockEntity) {
			// get the backing tile entity
			IVaultBlockEntity vaultTileEntity = (IVaultBlockEntity) tileEntity;

			// get the owner by uuid
			String playerUUID = vaultTileEntity.getOwnerUuid();
			Player vaultOwnerPlayer = null;
			if (playerUUID != null && !playerUUID.isEmpty()) {
				try {
					vaultOwnerPlayer = world.getPlayerByUUID(UUID.fromString(playerUUID));
				}
				catch(Exception e) {
					LegacyVault.LOGGER.error("unable to get player by uuid -> " + playerUUID, e);
				}
			}

//			if (vaultOwnerPlayer != null) {
//				// get  player capabilities
//				IPlayerVaultsHandler cap = vaultOwnerPlayer.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
//					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
//				});
//				LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
//
//				if (Config.GENERAL.enableLimitedVaults.get()) {
//					// decrement cap vault branch count
//					if (cap.getCount() > 0) {
//						// decrement count
//						int count = cap.getCount() - 1;
//						count = count < 0 ? 0 : count;
//						cap.setCount(count);
//
//						LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
//						// send state message to client
//						VaultCountMessageToClient message = new VaultCountMessageToClient(playerUUID, count);
//						ServerPlayer serverPlayer = (ServerPlayer)vaultOwnerPlayer;
//						LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),message);
//					}
//				}
//
//				// remove location
//				ICoords vaultLocation = new Coords(pos);
//				List<ICoords> newLocations = new ArrayList<>();
//				for (ICoords location : cap.getLocations()) {
//					if (!location.equals(vaultLocation)) {
//						newLocations.add(location);
//					}
//				}
//				cap.setLocations(newLocations);
//			}
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

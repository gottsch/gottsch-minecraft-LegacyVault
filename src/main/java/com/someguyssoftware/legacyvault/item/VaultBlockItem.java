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
package com.someguyssoftware.legacyvault.item;

import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.capability.IPlayerVaultsHandler;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;
import com.someguyssoftware.legacyvault.network.VaultCountMessageToClient;
import com.someguyssoftware.legacyvault.util.LegacyVaultHelper;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author Mark Gottschling on May 25, 2021
 *
 */
public class VaultBlockItem extends BlockItem {

	/**
	 * 
	 * @param block
	 * @param properties
	 */
	public VaultBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	/**
	 * 
	 */
	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		if (WorldInfo.isServerSide(context.getLevel())) {
			
			/*
			 *  Q: why add this creative check ? what is the reasoning?
			 *  A: the Block.playerDestroy() is only called in survival mode, thus the decrement vault count calculation is not executed, 
			 *  which will throw off the vault count. so just making Creative actions the same for placement and destroy.
			 */
			
			if (context.getPlayer().isCreative()) {
				return context.getLevel().setBlock(context.getClickedPos(), state, 26);
			}			
			
			if (Config.PUBLIC_VAULT.enablePublicVault.get()) {
				return false;
				// TODO how does Admin place then?
			}
			else {
				
				// get  player capabilities
				IPlayerVaultsHandler cap = LegacyVaultHelper.getPlayerCapability(context.getPlayer());
				LegacyVault.LOGGER.debug("player vault count -> {}", cap.getCount());
				
				if (Config.GENERAL.enableLimitedVaults.get()) {
					if (cap != null && cap.getCount() < Config.GENERAL.vaultsPerPlayer.get()) {
						LegacyVault.LOGGER.debug("player branch count less than config -> {}", Config.GENERAL.vaultsPerPlayer.get());
						
						// TODO does the updates to capability happen here or in VaultBlock ? 
						// increment capability size
						int count = cap.getCount() + 1;
						count = count > Config.GENERAL.vaultsPerPlayer.get() ? Config.GENERAL.vaultsPerPlayer.get() : count;
						cap.setCount(count);
						
						// send state message to client
						VaultCountMessageToClient message = new VaultCountMessageToClient(context.getPlayer().getStringUUID(), count);
						LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)context.getPlayer()),message);
					}
					else {
						LegacyVault.LOGGER.debug("player branch count greater than config-> {}",  Config.GENERAL.vaultsPerPlayer.get());
						return false;
					}
				}
					
				// add the vault location to capabilities
				ICoords location = new Coords(context.getClickedPos());
				cap.getLocations().add(location);

				return context.getLevel().setBlock(context.getClickedPos(), state, 26);
			}
		}
		else {
			LegacyVault.LOGGER.debug("no can do, you're on client side");
		}
		return false;
	}
}

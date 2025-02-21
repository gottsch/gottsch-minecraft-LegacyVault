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
package mod.gottsch.forge.legacyvault.core.util;

import java.util.UUID;

import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


/**
 * @author Mark Gottschling on Jun 6, 2021
 *
 */
public class ModUtil {

	public static IPlayerVaultsHandler getPlayerCapability(Player player) {
		IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
			return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
		});
		return cap;
	}

	/**
	 * 
	 * @param world
	 * @param playerUUID
	 * @return
	 */
	public static IPlayerVaultsHandler getPlayerCapability(Level world, String playerUUID) {
		Player player = world.getPlayerByUUID(UUID.fromString(playerUUID));
		if (player != null) {
			IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
				return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
			});
			return cap;
		}
		return null;
	}

	/**
	 * 
	 * @param world
	 * @param playerUUID
	 * @return
	 */
	public static boolean doesPlayerHaveCommunityAccess(Level world, String playerUUID) {
		Player player = world.getPlayerByUUID(UUID.fromString(playerUUID));
		return doesPlayerHaveCommunityAccess(player);
	}
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean doesPlayerHaveCommunityAccess(Player player) {
		if (player == null) {
			return false;
		}

		if (ServerConfig.COMMUNITY.communityVault.get()) {
			if (!ServerConfig.COMMUNITY.playerWhiteList.get().isEmpty()) {
				// check that player is part of white list
				for (String whiteListedUUID : ServerConfig.COMMUNITY.playerWhiteList.get()) {
					if (whiteListedUUID.equalsIgnoreCase(player.getStringUUID())) {
						return true;
					}
				}
			}
			else if (!ServerConfig.COMMUNITY.playerBlackList.get().isEmpty()) {
				// check that player is not part of black list
				for (String blackListedUUID : ServerConfig.COMMUNITY.playerWhiteList.get()) {
					if (blackListedUUID.equalsIgnoreCase(player.getStringUUID())) {
						return false;
					}
				}
			}
			else {
				// both lists are empty so everyone has access
				return true;
			}
		}
		return false;
	}
}

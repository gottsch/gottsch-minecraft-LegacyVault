/**
 * 
 */
package com.someguyssoftware.legacyvault.util;

import java.util.UUID;

import com.someguyssoftware.legacyvault.capability.IPlayerVaultsHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Jun 6, 2021
 *
 */
public class LegacyVaultHelper {

	public static IPlayerVaultsHandler getPlayerCapability(PlayerEntity player) {
		IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
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
	public static IPlayerVaultsHandler getPlayerCapability(World world, String playerUUID) {
		PlayerEntity player = world.getPlayerByUUID(UUID.fromString(playerUUID));
		if (player != null) {
			IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
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
	public static boolean doesPlayerHavePulicAccess(World world, String playerUUID) {
		PlayerEntity player = world.getPlayerByUUID(UUID.fromString(playerUUID));
		return doesPlayerHavePulicAccess(player);
	}
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public static boolean doesPlayerHavePulicAccess(PlayerEntity player) {
		if (player == null) {
			return false;
		}

		if (Config.PUBLIC_VAULT.enablePublicVault.get()) {
			if (!Config.PUBLIC_VAULT.playerWhiteList.get().isEmpty()) {
				// check that player is part of white list
				for (String whiteListedUUID : Config.PUBLIC_VAULT.playerWhiteList.get()) {
					if (whiteListedUUID.equalsIgnoreCase(player.getStringUUID())) {
						return true;
					}
				}
			}
			else if (!Config.PUBLIC_VAULT.playerBlackList.get().isEmpty()) {
				// check that player is not part of black list
				for (String blackListedUUID : Config.PUBLIC_VAULT.playerWhiteList.get()) {
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

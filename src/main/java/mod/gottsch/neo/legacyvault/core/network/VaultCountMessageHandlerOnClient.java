/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.core.network;

import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.neo.legacyvault.core.capability.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-only. Referenced from {@link LegacyVaultNetworking} through a lambda so a
 * dedicated server never loads it.
 *
 * @author Mark Gottschling on Jun 2, 2021
 *
 */
public class VaultCountMessageHandlerOnClient {

	/**
	 * Called when a message is received of the appropriate type.
	 * Payload handlers run on the main (client) thread by default in NeoForge 21.1.
	 */
	public static void onMessageReceived(final VaultCountMessageToClient message, IPayloadContext ctx) {
		LegacyVault.LOGGER.debug("received message at client -> {}", message);

		/*
		 * UUID validation: the server should only ever send a player their own count,
		 * so a mismatch with the local player means something unexpected is happening.
		 */
		Player localPlayer = ctx.player();
		if (localPlayer == null || !localPlayer.getStringUUID().equals(message.playerUUID())) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient: UUID mismatch — expected {}, got {}",
					localPlayer != null ? localPlayer.getStringUUID() : "null", message.playerUUID());
			return;
		}

		IPlayerVaultsHandler cap = ModAttachments.getPlayerVaults(localPlayer).orElse(null);
		if (cap != null) {
			LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
			cap.setCount(message.vaultCount());
			LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
		}
	}
}

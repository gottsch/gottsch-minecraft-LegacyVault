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
package mod.gottsch.forge.legacyvault.core.network;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * @author Mark Gottschling on Jun 2, 2021
 *
 */
public class VaultCountMessageHandlerOnClient {

	/**
	 * Called when a message is received of the appropriate type.
	 * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
	 */
	public static void onMessageReceived(final VaultCountMessageToClient message, Supplier<NetworkEvent.Context> ctxSupplier) {
		LegacyVault.LOGGER.debug("received message at client -> {}", message);
		NetworkEvent.Context ctx = ctxSupplier.get();
		if (!message.isMessageValid()) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient was invalid -> {}", message.toString());
			return;
		}

		ctx.enqueueWork(() ->
			// make sure it's only executed on the physical client
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> processMessage(ctx, message))
				);
		ctx.setPacketHandled(true);
	}

	private static void processMessage(Context ctx, VaultCountMessageToClient message) {
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		if (sideReceived != LogicalSide.CLIENT) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient received on wrong side -> {}", sideReceived);
			return;
		}

		/*
		 * UUID validation: this packet is PLAY_TO_CLIENT, so ctx.getSender() is not
		 * available here. Instead we verify that the UUID in the packet matches the
		 * local player — the server should only ever send a player their own count, so
		 * a mismatch means something unexpected is happening.
		 */
		Player localPlayer = Minecraft.getInstance().player;
		if (localPlayer == null || !localPlayer.getStringUUID().equals(message.getPlayerUUID())) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient: UUID mismatch — expected {}, got {}",
					localPlayer != null ? localPlayer.getStringUUID() : "null", message.getPlayerUUID());
			return;
		}

		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient: client world not available");
			return;
		}
		Level level = clientWorld.get();

		LegacyVault.LOGGER.debug("processing message");
		try {
			UUID uuid = UUID.fromString(message.getPlayerUUID());
			Player player = level.getPlayerByUUID(uuid);
			if (player != null) {
				IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElse(null);
				if (cap != null) {
					LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
					cap.setCount(message.getVaultCount());
					LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
				}
			}
		} catch (IllegalArgumentException e) {
			LegacyVault.LOGGER.error("VaultCountMessageToClient: invalid player UUID -> {}", message.getPlayerUUID(), e);
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Unexpected error processing VaultCountMessageToClient -> ", e);
		}
	}
}

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
package mod.gottsch.forge.legacyvault.network;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
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
		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		LegacyVault.LOGGER.debug("world -> {}", clientWorld.get());
		if (sideReceived != LogicalSide.CLIENT) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient received on wrong side -> {}", ctx.getDirection().getReceptionSide());
			return;
		}		
		Level level = clientWorld.get();
		
		LegacyVault.LOGGER.debug("processing message");
		try {
			Player player = level.getPlayerByUUID(UUID.fromString(message.getPlayerUUID()));
			if (player != null) {
				// get  player capabilities
				IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
				});
				LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
				cap.setCount(message.getVaultCount());
				LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
			}
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error("Unexpected error -> ", e);
		}
	}

	// this message is called from the Client thread.
	// it spawns a number of Particle particles at the target location within a short range around the target location
	private static void processMessage(Level worldClient, VaultCountMessageToClient message) {
		LegacyVault.LOGGER.debug("processing message");
		try {
			Player player = worldClient.getPlayerByUUID(UUID.fromString(message.getPlayerUUID()));
			if (player != null) {
				// get  player capabilities
				IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
				});
				LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
				cap.setCount(message.getVaultCount());
				LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
			}
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error("Unexpected error -> ", e);
		}
	}
}

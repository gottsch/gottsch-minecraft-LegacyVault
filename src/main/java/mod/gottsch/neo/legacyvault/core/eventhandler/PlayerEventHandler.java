/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.core.eventhandler;

import mod.gottsch.neo.gottschcore.spatial.DimensionCoords;
import mod.gottsch.neo.gottschcore.world.WorldInfo;
import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.block.ILegacyVaultBlock;
import mod.gottsch.neo.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.neo.legacyvault.core.capability.ModAttachments;
import mod.gottsch.neo.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.neo.legacyvault.core.network.VaultCountMessageToClient;
import mod.gottsch.neo.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
@EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerEventHandler {

	// player vault files will be loaded when player joins level
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		LegacyVault.LOGGER.debug("player is logging in.");

		// load the players persistent vault inventory (also resolves and applies tier from the vault file)
		VaultPersistenceManager.load(event.getEntity());

		// remove any stale location entries (vault was destroyed while player was offline)
		validateVaultLocations((ServerPlayer) event.getEntity());

		// update client players capabilities
		syncVaultCount((ServerPlayer) event.getEntity());
	}

	/*
	 * respawn creates a new client player with an empty attachment, so resend the count
	 * (the server copy survives via copyOnDeath). Also covers returning from the End.
	 */
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			syncVaultCount(player);
		}
	}

	/*
	 * a dimension change also sends a respawn packet, so the client gets a new LocalPlayer here too
	 */
	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			syncVaultCount(player);
		}
	}

	private static void syncVaultCount(ServerPlayer player) {
		if (!ServerConfig.COMMUNITY.enabled.get() && !ServerConfig.PERSONAL.unlimitedVaults.get()) {
			IPlayerVaultsHandler cap = ModAttachments.getPlayerVaults(player).orElse(null);
			if (cap == null) {
				LegacyVault.LOGGER.warn("player {} is missing PlayerVaultsHandler capability", player.getStringUUID());
			} else {
				LegacyVault.LOGGER.debug("player cap branch count -> {}", cap.getCount());
				VaultCountMessageToClient message = new VaultCountMessageToClient(player.getStringUUID(), cap.getCount());
				LegacyVault.LOGGER.debug("sending message to client -> {}", message);
				PacketDistributor.sendToPlayer(player, message);
			}
		}
	}

	private static void validateVaultLocations(ServerPlayer player) {
		IPlayerVaultsHandler cap = ModAttachments.getPlayerVaults(player).orElse(null);
		if (cap == null || cap.getLocations().isEmpty()) {
			return;
		}

		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}

		List<DimensionCoords> valid = new ArrayList<>();
		for (DimensionCoords loc : cap.getLocations()) {
			ServerLevel level = server.getLevel(loc.getDimension());
			if (level == null) {
				LegacyVault.LOGGER.warn("login validation: dimension {} not loaded for vault at {} owned by {}; skipping",
						loc.getDimension().location(), loc.toShortString(), player.getScoreboardName());
				continue;
			}
			if (level.getBlockState(loc.toPos()).getBlock() instanceof ILegacyVaultBlock) {
				valid.add(loc);
			} else {
				LegacyVault.LOGGER.warn("login validation: removed stale vault location {} for player {}",
						loc.toShortString(), player.getScoreboardName());
			}
		}

		if (valid.size() != cap.getLocations().size()) {
			cap.setLocations(valid);
			if (!ServerConfig.PERSONAL.unlimitedVaults.get()) {
				cap.setCount(valid.size());
				// the updated count is synced to the client by the existing code below
			}
		}
	}

	// player vault files will be saved when player leaves level
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		LegacyVault.LOGGER.debug("player is logging out");

		VaultPersistenceManager.save(event.getEntity());

	}
}

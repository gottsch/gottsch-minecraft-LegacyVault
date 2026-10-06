/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.core.network;

import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.inventory.CommunityVaultContainerMenu;
import mod.gottsch.neo.legacyvault.core.inventory.PersonalVaultContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server-side handler for {@link SortVaultPacket}.
 * Delegates to PersonalVaultContainerMenu.sortInventory() which sorts, saves,
 * and broadcasts the updated slots back to the client.
 *
 * @author Mark Gottschling on 2026
 */
public class SortVaultPacketHandler {

    /**
     * Payload handlers run on the main thread by default in NeoForge 21.1.
     */
    public static void onMessageReceived(SortVaultPacket message, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer player)) {
            LegacyVault.LOGGER.warn("SortVaultPacket: received with no server player");
            return;
        }
        if (player.containerMenu instanceof PersonalVaultContainerMenu menu) {
            menu.sortInventory(player);
            LegacyVault.LOGGER.debug("SortVaultPacket: sorted personal vault for player {}", player.getScoreboardName());
        } else if (player.containerMenu instanceof CommunityVaultContainerMenu menu) {
            menu.sortInventory(player);
            LegacyVault.LOGGER.debug("SortVaultPacket: sorted community vault for player {}", player.getScoreboardName());
        } else {
            LegacyVault.LOGGER.debug("SortVaultPacket: player {} does not have a vault open", player.getScoreboardName());
        }
    }
}

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
package mod.gottsch.forge.legacyvault.core.network;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.inventory.PersonalVaultContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-side handler for {@link SortVaultPacket}.
 * Delegates to PersonalVaultContainerMenu.sortInventory() which sorts, saves,
 * and broadcasts the updated slots back to the client.
 *
 * @author Mark Gottschling on 2026
 */
public class SortVaultPacketHandler {

    public static void onMessageReceived(SortVaultPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                LegacyVault.LOGGER.warn("SortVaultPacket: received with no sender");
                return;
            }
            if (player.containerMenu instanceof PersonalVaultContainerMenu menu) {
                menu.sortInventory(player);
                LegacyVault.LOGGER.debug("SortVaultPacket: sorted vault for player {}", player.getScoreboardName());
            } else {
                LegacyVault.LOGGER.debug("SortVaultPacket: player {} does not have a vault open", player.getScoreboardName());
            }
        });
        ctx.setPacketHandled(true);
    }
}

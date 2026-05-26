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

import net.minecraft.network.FriendlyByteBuf;

/**
 * Client → Server: requests the server to sort the sending player's vault inventory.
 * No payload — the server identifies the player from the network context.
 *
 * @author Mark Gottschling on 2026
 */
public class SortVaultPacket {

    public SortVaultPacket() {}

    public static SortVaultPacket decode(FriendlyByteBuf buf) {
        return new SortVaultPacket();
    }

    public void encode(FriendlyByteBuf buf) {
        // no payload
    }
}

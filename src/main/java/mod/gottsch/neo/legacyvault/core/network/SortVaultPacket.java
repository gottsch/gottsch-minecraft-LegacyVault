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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Client → Server: requests the server to sort the sending player's vault inventory.
 * No payload — the server identifies the player from the network context.
 *
 * @author Mark Gottschling on 2026
 */
public class SortVaultPacket implements CustomPacketPayload {
    public static final SortVaultPacket INSTANCE = new SortVaultPacket();

    public static final CustomPacketPayload.Type<SortVaultPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(LegacyVault.MOD_ID, "sort_vault"));

    public static final StreamCodec<FriendlyByteBuf, SortVaultPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private SortVaultPacket() {}

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server → Client: the player's current personal vault count.
 *
 * @author Mark Gottschling on Jun 2, 2021
 *
 */
public record VaultCountMessageToClient(String playerUUID, int vaultCount) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<VaultCountMessageToClient> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(LegacyVault.MOD_ID, "vault_count"));

	public static final StreamCodec<FriendlyByteBuf, VaultCountMessageToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, VaultCountMessageToClient::playerUUID,
			ByteBufCodecs.INT, VaultCountMessageToClient::vaultCount,
			VaultCountMessageToClient::new);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

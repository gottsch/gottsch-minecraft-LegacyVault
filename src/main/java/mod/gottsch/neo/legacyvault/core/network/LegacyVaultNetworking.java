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
package mod.gottsch.neo.legacyvault.core.network;

import mod.gottsch.neo.legacyvault.core.LegacyVault;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * @author Mark Gottschling on Jun 3, 2021
 *
 */
public class LegacyVaultNetworking {
	public static final String PROTOCOL_VERSION = "1.0";

	public static void register(IEventBus modEventBus) {
		modEventBus.addListener(LegacyVaultNetworking::onRegisterPayloads);
	}

	private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(LegacyVault.MOD_ID).versioned(PROTOCOL_VERSION);

		// lambda (not a method ref) so the client-only handler class is never loaded on a dedicated server
		registrar.playToClient(
				VaultCountMessageToClient.TYPE,
				VaultCountMessageToClient.STREAM_CODEC,
				(message, ctx) -> VaultCountMessageHandlerOnClient.onMessageReceived(message, ctx)
		);
		registrar.playToServer(
				SortVaultPacket.TYPE,
				SortVaultPacket.STREAM_CODEC,
				SortVaultPacketHandler::onMessageReceived
		);
	}
}

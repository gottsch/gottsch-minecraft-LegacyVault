/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.network;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

import java.util.Optional;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


/**
 * @author Mark Gottschling on Jun 3, 2021
 *
 */
public class LegacyVaultNetworking {
	
	public static final String PROTOCOL_VERSION = "1.0";
	public static final int VAULT_COUNT_MESSAGE_ID = 14;	
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(LegacyVault.MODID, "legacy_vault_channel");
	
	public static SimpleChannel channel;    // used to transmit your network messages

	/**
	 * 
	 * @param event
	 */
	public static void register() {
		// register the channel
//		channel = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> PROTOCOL_VERSION,
//	            VaultCountMessageHandlerOnClient::isThisProtocolAcceptedByClient,
//	            VaultCountMessageHandlerOnServer::isThisProtocolAcceptedByServer);
		
		channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
				.networkProtocolVersion(() -> PROTOCOL_VERSION)
				.clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals)
				.simpleChannel();
		
		// register the message
		channel.registerMessage(VAULT_COUNT_MESSAGE_ID, VaultCountMessageToClient.class,
	            VaultCountMessageToClient::encode, VaultCountMessageToClient::decode,
	            VaultCountMessageHandlerOnClient::onMessageReceived,
	            Optional.of(PLAY_TO_CLIENT));
	}
}

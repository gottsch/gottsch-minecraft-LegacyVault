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
package com.someguyssoftware.legacyvault.init;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IPlayerVaultsHandler;
import com.someguyssoftware.legacyvault.capability.PlayerVaultsHandler;
import com.someguyssoftware.legacyvault.capability.PlayerVaultsStorage;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.exception.DbInitializationException;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
public class LegacyVaultSetup {
	// TODO should move to new class like LegacyVaultNetworking ?
//	public static SimpleChannel simpleChannel;    // used to transmit your network messages
//	public static final String MESSAGE_PROTOCOL_VERSION = "1.0";
//	public static final int VAULT_COUNT_MESSAGE_ID = 14;	
//	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(LegacyVault.MODID, "legacy_vault_channel");
//	
	// TODO this is stopping the world on client side, stopping the server on server side.
	public static void serverStopping(final FMLServerStoppingEvent event) {
		LegacyVault.LOGGER.debug("server stopping event");
		try {
			DbManager.shutdown();
		} catch (Exception e) {
			LegacyVault.LOGGER.error("Error stopping database:", e);
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	public static void common(final FMLCommonSetupEvent event) {
		// add mod specific logging
		IModSetup.addRollingFileAppender(LegacyVault.instance.getName(), null);
		
		// start the database
		
//		8/12/21 - move this to WorldEvent.Load event so it loads on every world load
//		try {
//			DbManager.start((Config) LegacyVault.instance.getConfig());
//		} catch (DbInitializationException e) {
//			LegacyVault.LOGGER.error("Unable to start database manager:", e);
////			getConfig().setModEnabled(false);
//			// TODO create another PlayerLoggedIn Event that checks if the database failed initialization and inform player.
//		}
		
		// attach capabilities
		CapabilityManager.INSTANCE.register(IPlayerVaultsHandler.class, new PlayerVaultsStorage(), PlayerVaultsHandler::new);
		
		/*
		 *  networking
		 */		
//		// register the channel
//		simpleChannel = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> MESSAGE_PROTOCOL_VERSION,
//	            VaultCountMessageHandlerOnClient::isThisProtocolAcceptedByClient,
//	            VaultCountMessageHandlerOnServer::isThisProtocolAcceptedByServer);
//		
//		// register the message
//		simpleChannel.registerMessage(VAULT_COUNT_MESSAGE_ID, VaultCountMessageToClient.class,
//	            VaultCountMessageToClient::encode, VaultCountMessageToClient::decode,
//	            VaultCountMessageHandlerOnClient::onMessageReceived,
//	            Optional.of(PLAY_TO_CLIENT));
	}
}

package com.someguyssoftware.legacyvault.network;

/**
 * 
 * @author Mark Gottschling on Jun 2, 2021
 *
 */
public class VaultCountMessageHandlerOnServer {

	/**
	 * 
	 * @param protocolVersion
	 * @return
	 */
	public static boolean isThisProtocolAcceptedByServer(String protocolVersion) {
		return LegacyVaultNetworking.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
	}
}

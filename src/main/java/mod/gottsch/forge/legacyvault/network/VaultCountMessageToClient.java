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
package mod.gottsch.forge.legacyvault.network;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author Mark Gottschling on Jun 2, 2021
 *
 */
public class VaultCountMessageToClient {
	
	private String playerUUID;
	private int vaultCount = 0;
	private boolean messageIsValid;
	
	public VaultCountMessageToClient() {
		messageIsValid = false;
	}
	
	public VaultCountMessageToClient(String playerUUID, int count) {
		this.playerUUID = playerUUID;
		this.vaultCount = count;
		messageIsValid = true;
	}
	
	/**
	 * 
	 * @param buf
	 * @return
	 */
	public static VaultCountMessageToClient decode(FriendlyByteBuf buf) {
		VaultCountMessageToClient message = new VaultCountMessageToClient();
		try {
			message.playerUUID = buf.readUtf();
			message.vaultCount = buf.readInt();
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error("An error occurred attempting to read message: ", e);
			return message;
		}
		message.setMessageIsValid( true);
		return message;
	}
	
	/**
	 * 
	 * @param buf
	 */
	public void encode(FriendlyByteBuf buf) {
		if (!messageIsValid) {
			return;
		}
		buf.writeUtf(getPlayerUUID());
		buf.writeInt(getVaultCount());
	}
	
	public boolean isMessageValid() {
	    return messageIsValid;
	  }

	public String getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(String playerUUID) {
		this.playerUUID = playerUUID;
	}

	public int getVaultCount() {
		return vaultCount;
	}

	public void setVaultCount(int vaultCount) {
		this.vaultCount = vaultCount;
	}

	public boolean isMessageIsValid() {
		return messageIsValid;
	}

	public void setMessageIsValid(boolean messageIsValid) {
		this.messageIsValid = messageIsValid;
	}

	@Override
	public String toString() {
		return "VaultCountMessageToClient [playerUUID=" + playerUUID + ", vaultCount=" + vaultCount
				+ ", messageIsValid=" + messageIsValid + "]";
	}
}

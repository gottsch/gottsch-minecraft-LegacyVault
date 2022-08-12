/**
 * 
 */
package mod.gottsch.forge.legacyvault.network;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

/**
 * @author Mark Gottschling on Jun 2, 2021
 *
 */
public class VaultCountMessageHandlerOnClient {

	/**
	 * 
	 * @param protocolVersion
	 * @return
	 */
	public static boolean isThisProtocolAcceptedByClient(String protocolVersion) {
		return LegacyVaultNetworking.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
	}

	/**
	 * Called when a message is received of the appropriate type.
	 * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
	 */
	public static void onMessageReceived(final VaultCountMessageToClient message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		ctx.setPacketHandled(true);

		if (sideReceived != LogicalSide.CLIENT) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient received on wrong side -> {}", ctx.getDirection().getReceptionSide());
			return;
		}
		if (!message.isMessageValid()) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient was invalid -> {}", message.toString());
			return;
		}
		// we know for sure that this handler is only used on the client side, so it is ok to assume
		//  that the ctx handler is a client, and that Minecraft exists.
		// Packets received on the server side must be handled differently!  See VaultCountMessageHandlerOnServer

		Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			LegacyVault.LOGGER.warn("VaultCountMessageToClient context could not provide a ClientWorld.");
			return;
		}

		// This code creates a new task which will be executed by the client during the next tick
		//  In this case, the task is to call messageHandlerOnClient.processMessage(worldclient, message)
		ctx.enqueueWork(() -> processMessage(clientWorld.get(), message));
	}

	// this message is called from the Client thread.
	// it spawns a number of Particle particles at the target location within a short range around the target location
	private static void processMessage(Level worldClient, VaultCountMessageToClient message) {
		try {
			Player player = worldClient.getPlayerByUUID(UUID.fromString(message.getPlayerUUID()));
//			if (player != null) {
//				// get  player capabilities
//				IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
//					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
//				});
//				LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());
//				cap.setCount(message.getVaultCount());
//				LegacyVault.LOGGER.debug("player new branch count -> {}", cap.getCount());
//			}
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error("Unexpected error -> ", e);
		}
	}
}

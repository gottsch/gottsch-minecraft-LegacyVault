/**
 * 
 */
package com.someguyssoftware.legacyvault.eventhandler;

import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IVaultCountHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.capability.PlayerCapabilityProvider;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.network.LegacyVaultNetworking;
import com.someguyssoftware.legacyvault.network.VaultCountMessageToClient;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof PlayerEntity)) return;

		event.addCapability(new ResourceLocation(LegacyVault.MODID, Config.CapabilityID.PLAYER_PROVIDER), new PlayerCapabilityProvider());
	}

	@SubscribeEvent
	public static void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event) {

		if (WorldInfo.isClientSide(event.getPlayer().level)) {
			return;
		}
		
		// update client players capabilities
		if (!Config.GENERAL.enablePublicVault.get() &&  Config.GENERAL.enableLimitedVaults.get()) {
			// get  player capabilities
			IVaultCountHandler cap = event.getPlayer().getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
				return new RuntimeException("player does not have VaultCountHandler capability.'");
			});
			LegacyVault.LOGGER.debug("player branch count -> {}", cap.getCount());

			if (cap != null) {
				// send state message to client
				VaultCountMessageToClient message = new VaultCountMessageToClient(event.getPlayer().getStringUUID(), cap.getCount());
				LegacyVaultNetworking.simpleChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()),message);
			}
		}
	}

}

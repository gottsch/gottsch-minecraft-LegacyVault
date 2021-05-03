/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

import com.someguyssoftware.legacyvault.LegacyVault;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class LegacyVaultContainers {
	public static ContainerType<VaultContainer> STANDARD_VAULT_CONTAINER_TYPE;

	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)	
	public static class RegistrationHandler {		
		
		@SubscribeEvent
		public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
			STANDARD_VAULT_CONTAINER_TYPE = IForgeContainerType.create(VaultContainer::create);
			STANDARD_VAULT_CONTAINER_TYPE.setRegistryName("standard_legacy_vault_container");
			event.getRegistry().register(STANDARD_VAULT_CONTAINER_TYPE);
		}
	}
}

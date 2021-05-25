/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.Config;

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
	public static ContainerType<MediumVaultContainer> MEDIUM_VAULT_CONTAINER_TYPE;
	public static ContainerType<LargeVaultContainer> LARGE_VAULT_CONTAINER_TYPE;
	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)	
	public static class RegistrationHandler {		
		
		@SubscribeEvent
		public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
			STANDARD_VAULT_CONTAINER_TYPE = IForgeContainerType.create(VaultContainer::create);
			STANDARD_VAULT_CONTAINER_TYPE.setRegistryName(Config.ContainerID.VAULT_CONTAINER);
			event.getRegistry().register(STANDARD_VAULT_CONTAINER_TYPE);
			
			MEDIUM_VAULT_CONTAINER_TYPE = IForgeContainerType.create(MediumVaultContainer::create);
			MEDIUM_VAULT_CONTAINER_TYPE.setRegistryName(Config.ContainerID.MEDIUM_VAULT_CONTAINER);
			event.getRegistry().register(MEDIUM_VAULT_CONTAINER_TYPE);
			
			LARGE_VAULT_CONTAINER_TYPE = IForgeContainerType.create(LargeVaultContainer::create);
			LARGE_VAULT_CONTAINER_TYPE.setRegistryName(Config.ContainerID.LARGE_VAULT_CONTAINER);
			event.getRegistry().register(LARGE_VAULT_CONTAINER_TYPE);
		}
	}
}

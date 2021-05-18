/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.LegacyVaultBlocks;
import com.someguyssoftware.legacyvault.gui.render.tileentity.SteamPunkVaultTileEntityRenderer;
import com.someguyssoftware.legacyvault.gui.render.tileentity.VaultTileEntityRenderer;
import com.someguyssoftware.legacyvault.inventory.LegacyVaultContainers;
import com.someguyssoftware.legacyvault.tileentity.LegacyVaultTileEntities;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 
 * @author Mark Gottschling on Apr 30, 2021
 *
 */
public class LegacyVaultGuis {
	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
	public static class RegistrationHandler {
		// register the factory that is used on the client to generate Screen corresponding to our Container
		@SubscribeEvent
		public static void onClientSetupEvent(FMLClientSetupEvent event) {
//			ScreenManager.register(LegacyVaultContainers.STANDARD_VAULT_CONTAINER_TYPE, StandardVaultContainerScreen::new);
			ScreenManager.register(LegacyVaultContainers.STANDARD_VAULT_CONTAINER_TYPE, ScrollableVaultContainerScreen2::new);
			
			// tell the renderer that the base is rendered using CUTOUT_MIPPED (to match the Block Hopper)
			RenderTypeLookup.setRenderLayer(LegacyVaultBlocks.VAULT, RenderType.cutoutMipped());
            
			// register the custom renderer for our tile entity
//			ClientRegistry.bindTileEntityRenderer(LegacyVaultTileEntities.VAULT_TILE_ENTITY_TYPE, VaultTileEntityRenderer::new);
			ClientRegistry.bindTileEntityRenderer(LegacyVaultTileEntities.VAULT_TILE_ENTITY_TYPE, SteamPunkVaultTileEntityRenderer::new);

		}
	}
}

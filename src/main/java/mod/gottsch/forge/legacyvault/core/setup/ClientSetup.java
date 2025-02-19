/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.setup;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import mod.gottsch.forge.legacyvault.core.block.entity.ModBlockEntities;
import mod.gottsch.forge.legacyvault.core.client.model.RusticVaultModel;
import mod.gottsch.forge.legacyvault.core.client.model.ClassicVaultModel;
import mod.gottsch.forge.legacyvault.core.client.renderer.RusticVaultRenderer;
import mod.gottsch.forge.legacyvault.core.client.renderer.ClassicVaultRenderer;
import mod.gottsch.forge.legacyvault.core.gui.VaultScreen;
import mod.gottsch.forge.legacyvault.core.inventory.ModContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 
 * @author Mark Gottschling
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
	
	/**
	 * 
	 * @param event
	 */
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModContainers.VAULT_CONTAINER.get(), VaultScreen::new);  // attach our container to the screen
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.RUSTIC_VAULT.get(), RenderType.cutoutMipped());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLASSIC_VAULT.get(), RenderType.cutoutMipped());
        });
    }
    
	/**
	 * register renderers
	 * @param event
	 */
	@SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntities.RUSTIC_VAULT.get(), RusticVaultRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.CLASSIC_VAULT.get(), ClassicVaultRenderer::new);
	}
	
	/**
	 * register model layer definitions
	 * @param event
	 */
	@SubscribeEvent()
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClassicVaultModel.LAYER_LOCATION, ClassicVaultModel::createBodyLayer);
		event.registerLayerDefinition(RusticVaultModel.LAYER_LOCATION, RusticVaultModel::createBodyLayer);
	}
}

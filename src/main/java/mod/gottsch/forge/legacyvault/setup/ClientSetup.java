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
package mod.gottsch.forge.legacyvault.setup;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.client.model.VaultModel;
import mod.gottsch.forge.legacyvault.client.renderer.VaultRenderer;
import mod.gottsch.forge.legacyvault.gui.VaultScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.SpriteLoader.Preparations;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 
 * @author Mark Gottschling
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
	
	/**
	 * 
	 * @param event
	 */
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.VAULT_CONTAINER.get(), VaultScreen::new);           // attach our container to the screen
            ItemBlockRenderTypes.setRenderLayer(Registration.VAULT.get(), RenderType.cutoutMipped());
        });
    }
    
	/**
	 * register renderers
	 * @param event
	 */
	@SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(Registration.VAULT_BLOCK_ENTITY_TYPE.get(), VaultRenderer::new);
	}
	
	/**
	 * register model layer definitions
	 * @param event
	 */
	@SubscribeEvent()
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(VaultModel.LAYER_LOCATION, VaultModel::createBodyLayer);
	}
}

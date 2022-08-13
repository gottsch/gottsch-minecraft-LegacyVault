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
package mod.gottsch.forge.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.inventory.VaultSlotSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 
 * @author Mark Gottschling on Jun 21, 2022
 *
 */
public class VaultScreen extends AbstractContainerScreen<VaultContainerMenu> {
	// the resource locations for the background images of the GUI
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault1c.png");
	private static final ResourceLocation MEDIUM_BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault2c.png");
	private static final ResourceLocation LARGE_BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault3c.png");

	private ResourceLocation bgTexture;
	private Inventory inventory;
	/**
	 * 
	 * @param containerMenu
	 * @param inventory
	 * @param name
	 */
	public VaultScreen(VaultContainerMenu containerMenu, Inventory inventory, Component name) {
		super(containerMenu, inventory, name);

		this.inventory = inventory;
		
		if (ServerConfig.GENERAL.inventorySize.get() <= VaultSlotSize.SMALL.getSize()) {
			imageWidth = 176;
			imageHeight = 174;//167;
			bgTexture = BG_TEXTURE;
		}
		else if (ServerConfig.GENERAL.inventorySize.get() <= VaultSlotSize.MEDIUM.getSize()) {
			imageWidth = 176;
			imageHeight = 228;
			bgTexture = MEDIUM_BG_TEXTURE;
		}
		else {
			imageWidth = 247;
			imageHeight = 246;
			bgTexture = LARGE_BG_TEXTURE;
		}
	}

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		final int LABEL_XPOS = 5;
		final int FONT_Y_SPACING = 12;
		final int CHEST_LABEL_YPOS = getMenu().getTitleYPos() - FONT_Y_SPACING;
        drawString(matrixStack, Minecraft.getInstance().font, new TranslatableComponent("display.vault.name").getString(), LABEL_XPOS, CHEST_LABEL_YPOS, Color.WHITE.getRGB());

		String vaultsRemaining = "";
		if (ServerConfig.PUBLIC_VAULT.enablePublicVault.get()) {
			vaultsRemaining = new TranslatableComponent("display.public_vault").getString();
		}
		else {
			// check for unlimited
			if (ServerConfig.GENERAL.enableLimitedVaults.get()) {
				IPlayerVaultsHandler cap = inventory.player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
					return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
				});
				vaultsRemaining = new TranslatableComponent("display.vaults_remaining", String.valueOf(ServerConfig.GENERAL.vaultsPerPlayer.get() - cap.getCount()), ServerConfig.GENERAL.vaultsPerPlayer.get()).getString();
			}
			else {
				vaultsRemaining = new TranslatableComponent("display.unlimited_vaults").getString();
			}
		}
		this.font.draw(matrixStack, vaultsRemaining, LABEL_XPOS, getMenu().getVaultsRemainingYPos(),Color.darkGray.getRGB());

    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, getBgTexture());
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

	private ResourceLocation getBgTexture() {
		return bgTexture;
	}
}

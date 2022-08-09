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
package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.inventory.VaultContainerMenu;
import com.someguyssoftware.legacyvault.inventory.VaultSlotSize;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
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
	
	/**
	 * 
	 * @param containerMenu
	 * @param inventory
	 * @param name
	 */
	public VaultScreen(VaultContainerMenu containerMenu, Inventory inventory, Component name) {
		super(containerMenu, inventory, name);

		if (Config.GENERAL.inventorySize.get() <= VaultSlotSize.SMALL.getSize()) {
			imageWidth = 176;
			imageHeight = 174;//167;
			bgTexture = BG_TEXTURE;
		}
		else if (Config.GENERAL.inventorySize.get() <= VaultSlotSize.MEDIUM.getSize()) {
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
		final int FONT_Y_SPACING = 10;
		final int CHEST_LABEL_YPOS = getMenu().getTitleYPos() - FONT_Y_SPACING;
        drawString(matrixStack, Minecraft.getInstance().font, this.getTitle().getString(), LABEL_XPOS, CHEST_LABEL_YPOS, Color.darkGray.getRGB());

        // TODO add other titles
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

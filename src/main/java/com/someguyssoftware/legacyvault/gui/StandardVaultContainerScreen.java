package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class StandardVaultContainerScreen extends ContainerScreen<VaultContainer> {

	// this is the resource location for the background image for the GUI
	private static final ResourceLocation texture = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault.png");

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public StandardVaultContainerScreen(VaultContainer screenContainer, PlayerInventory playerInventory,
			ITextComponent title) {
		super(screenContainer, playerInventory, title);
		// Set the width and height of the gui.  Should match the size of the texture!
		imageWidth = 176;
		imageHeight = 167;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 * Taken directly from ChestScreen
	 */
	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		final float LABEL_XPOS = 5;
		final float FONT_Y_SPACING = 10;
		final float CHEST_LABEL_YPOS = getMenu().getContainerInventoryYPos() - FONT_Y_SPACING;
		font.draw(matrixStack, this.title.getString(), LABEL_XPOS, CHEST_LABEL_YPOS, Color.darkGray.getRGB());
		final float PLAYER_INV_LABEL_YPOS = getMenu().getPlayerInventoryYPos() - FONT_Y_SPACING;
		this.font.draw(matrixStack, this.inventory.getDisplayName().getString(), 
				LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(texture);

		// width and height are the size provided to the window when initialised after creation.
		// xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
		// The code below is typical for vanilla containers, so I've just copied that- it appears to centre the texture within
		//  the available window
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}

}
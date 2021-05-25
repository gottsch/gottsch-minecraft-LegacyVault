package com.someguyssoftware.legacyvault.gui;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.AbstractLegacyVaultContainer;
import com.someguyssoftware.legacyvault.inventory.LargeVaultContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class LargeVaultContainerScreen extends AbstractVaultContainerScreen<LargeVaultContainer> {

	// this is the resource location for the background image for the GUI
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault3.png");

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public LargeVaultContainerScreen(LargeVaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		imageHeight = 255;
	}

	public ResourceLocation getBgTexture() {
		return BG_TEXTURE;
	}
}

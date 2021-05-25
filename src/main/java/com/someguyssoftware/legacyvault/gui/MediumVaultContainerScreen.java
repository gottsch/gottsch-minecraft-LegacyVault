package com.someguyssoftware.legacyvault.gui;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.MediumVaultContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MediumVaultContainerScreen extends AbstractVaultContainerScreen<MediumVaultContainer> {

	// this is the resource location for the background image for the GUI
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault2.png");

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public MediumVaultContainerScreen(MediumVaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		imageHeight = 219;
	}

	public ResourceLocation getBgTexture() {
		return BG_TEXTURE;
	}
}

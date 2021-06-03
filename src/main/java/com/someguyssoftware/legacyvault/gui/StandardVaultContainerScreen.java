package com.someguyssoftware.legacyvault.gui;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class StandardVaultContainerScreen extends AbstractVaultContainerScreen<VaultContainer> {

	// this is the resource location for the background image for the GUI
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault1c.png");

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public StandardVaultContainerScreen(VaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		LegacyVault.LOGGER.info("creating StandardVaultContainerScreen");
	}

	public ResourceLocation getBgTexture() {
		return BG_TEXTURE;
	}
}

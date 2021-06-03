/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.capability.IVaultCountHandler;
import com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.inventory.AbstractLegacyVaultContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author Mark Gottschling on Apr 1, 2021
 *
 */
public abstract class AbstractVaultContainerScreen<T extends AbstractLegacyVaultContainer> extends ContainerScreen<T>{

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public AbstractVaultContainerScreen(T screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		// Set the width and height of the gui.  Should match the size of the BG_TEXTURE!
		imageWidth = 176;
		imageHeight = 174;//167;
	}

	@Override
   public void render(MatrixStack matrix, int mouseX, int mouseY, float p_230430_4_) {
	      this.renderBackground(matrix);
	      super.render(matrix, mouseX, mouseY, p_230430_4_);
	      this.renderTooltip(matrix, mouseX, mouseY);
	   }
	   
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 * Taken directly from ChestScreen
	 */
	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		final float LABEL_XPOS = 5;
		final float FONT_Y_SPACING = 10;
		final float CHEST_LABEL_YPOS = getMenu().getTitleYPos() - FONT_Y_SPACING; //getMenu().getContainerInventoryYPos() - FONT_Y_SPACING;
		font.draw(matrixStack, this.title.getString(), LABEL_XPOS, CHEST_LABEL_YPOS, Color.darkGray.getRGB());
		final float PLAYER_INV_LABEL_YPOS = getMenu().getPlayerInventoryYPos() - FONT_Y_SPACING;
		this.font.draw(matrixStack, this.inventory.getDisplayName().getString(), 
				LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
		
		//  add Vaults Remaining
		String vaultsRemainingValue = "";
		if (Config.GENERAL.enablePublicVault.get()) {
			vaultsRemainingValue = VAULTS_REMAINING.PUBLIC.getValue();
		}
		else {
			// check for unlimited
			if (Config.GENERAL.enableLimitedVaults.get()) {
//				LegacyVault.LOGGER.debug("player -> {}", inventory.player.getStringUUID());
				IVaultCountHandler cap = inventory.player.getCapability(LegacyVaultCapabilities.VAULT_BRANCH).orElseThrow(() -> {
					return new RuntimeException("player does not have VaultCountHandler capability.'");
				});
//				LegacyVault.LOGGER.debug("player vault count -> {}", cap.getCount());
				vaultsRemainingValue = String.valueOf(Config.GENERAL.vaultsPerPlayer.get() - cap.getCount());
//				LegacyVault.LOGGER.debug("vaults remaining -> {}", vaultsRemainingValue);
			}
			else {
				vaultsRemainingValue = VAULTS_REMAINING.UNLIMITED.getValue();
			}
		}
		this.font.draw(matrixStack, new TranslationTextComponent("display.vaults_remaining", vaultsRemainingValue, Config.GENERAL.vaultsPerPlayer.get()).getString(), LABEL_XPOS, getMenu().getVaultsRemainingYPos(),Color.darkGray.getRGB());
	}
	
	// TODO move
	public enum VAULTS_REMAINING {
		PUBLIC("Public Vault"),
		UNLIMITED("Unlimited"),
		VAULTS_PER("");

		String value;
		
		VAULTS_REMAINING(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}		
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(getBgTexture());

		// width and height are the size provided to the window when initialised after creation.
		// xSize, ySize are the expected size of the BG_TEXTURE-? usually seems to be left as a default.
		// The code below is typical for vanilla containers, so I've just copied that- it appears to centre the BG_TEXTURE within
		//  the available window
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	public abstract ResourceLocation getBgTexture();
}

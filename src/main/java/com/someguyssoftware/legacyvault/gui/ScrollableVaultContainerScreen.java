/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Mark Gottschling on May 13, 2021
 *
 */
@Deprecated
public class ScrollableVaultContainerScreen extends ContainerScreen<VaultContainer>{

	// this is the resource location for the background image for the GUI
	private static final ResourceLocation texture = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault.png");

	private float scrollOffset;
	private boolean isScrolling = false;
	private double currentScroll;

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public ScrollableVaultContainerScreen(VaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		this.imageHeight = 136;
		this.imageWidth = 195;
	}
//
//	/**
//	 * 
//	 */
//	@Override
//	public boolean mouseClicked(double mouseX, double mouseY, int button) {
//		if (button == 0) {
//			if (this.insideScrollbar(mouseX, mouseY)) {
//				this.isScrolling = this.canScroll();
//				return true;
//			}
//		}
//		return super.mouseClicked(mouseX, mouseY, button);
//	}
//
//	/**
//	 * 
//	 */
//	@Override
//	public boolean mouseReleased(double mouseX, double mouseY, int button) {
//		isScrolling = false;
//		return super.mouseReleased(mouseX, mouseY, button);
//	}
//
//	public boolean mouseScrolled(double mouseX, double mouseY, double b) {
//		if (!this.canScroll()) {
//			return false;
//		} else {
//			int i = ((this.menu).items.size() + 9 - 1) / 9 - 5;
//			this.scrollOffset = (float)((double)this.scrollOffset - b / (double)i);
//			this.scrollOffset = MathHelper.clamp(this.scrollOffset, 0.0F, 1.0F);
//			this.menu.scrollTo(this.scrollOffset);
//			return true;
//		}
//	}
//
//	public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
//		if (this.isScrolling) {
//			int i = this.topPos + 18;
//			int j = i + 112;
//			this.scrollOffset = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
//			this.scrollOffset = MathHelper.clamp(this.scrollOffset, 0.0F, 1.0F);
//			this.menu.scrollTo(this.scrollOffset);
//			return true;
//		} else {
//			return super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
//		}
//	}
//
//	/**
//	 * 
//	 * @param mouseX
//	 * @param mouseY
//	 * @return
//	 */
//	protected boolean insideScrollbar(double mouseX, double mouseY) {
//		int startX = this.leftPos + 175;
//		int startY = this.topPos + 18;
//		int endX = startX + 14;
//		int endY = startY + 112;
//		return mouseX >= (double)startX && mouseY >= (double)startY && mouseX < (double)endX && mouseY < (double)endY;
//	}
//
//	/**
//	 * 
//	 * @return
//	 */
//	private boolean canScroll() {
//		return this.menu.canScroll();
//	}

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
	protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
		// TODO Auto-generated method stub

	}

}

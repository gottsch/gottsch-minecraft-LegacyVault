/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Mark Gottschling on May 14, 2021
 *
 */
@Deprecated
public class ScrollableVaultContainerScreen2 extends ContainerScreen<VaultContainer> {
	// this is the resource location for the background image for the GUI
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault.png");

	// drag y
//	private final Set<Slot> drag_click = new HashSet<>();
	private Scrollbar myScrollBar = null;


	// scrollbar related variables (pixels related, not Slot related)
	private int perRow = 9;
	private int rows = 0;
	
	/**
	 * 
	 * @param container
	 * @param playerInventory
	 * @param title
	 */
	public ScrollableVaultContainerScreen2(VaultContainer container, PlayerInventory playerInventory,
			ITextComponent title) {
		super(container, playerInventory, title);
		// Set the width and height of the gui.  Should match the size of the BG_TEXTURE!
		imageWidth = 176;
		imageHeight = 167;

		// create a scroll bar
        setScrollBar(new Scrollbar());
        
//        initScrollBar();
	}

	@Override
	public void init() {
		super.init();
	}

	
	/**
	 * TODO move to abstract / template pattern
	 * TODO lots of magic numbers - change to constants or methods
	 */
//    private void initScrollBar() {
//        this.getScrollBar().setTop(18).setLeft(175).setHeight(this.rows * 18 - 2);
//        this.getScrollBar().setRange(0, (this.repo.size() + this.perRow - 1) / this.perRow - this.rows,
//                Math.max(1, this.rows / 6)); // 6 = number of gui rows
//        // this.rows is max rows in inventory
//   
//    }
    
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
		this.minecraft.getTextureManager().bind(BG_TEXTURE);

		// width and height are the size provided to the window when initialised after creation.
		// xSize, ySize are the expected size of the BG_TEXTURE-? usually seems to be left as a default.
		// The code below is typical for vanilla containers, so I've just copied that- it appears to centre the BG_TEXTURE within
		//  the available window
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}
	
	/**
	 * 
	 * @return
	 */
	private List<Slot> getInventorySlots() {
		return this.menu.slots;
	}

	@Override
	public void render(MatrixStack matrixStack, final int x, final int y, float partialTicks) {
		
		super.render(matrixStack, x, y, partialTicks);
		
		final int ox = this.getGuiLeft(); // (width - xSize) / 2;
		final int oy = this.getGuiTop(); // (height - ySize) / 2;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (this.getScrollBar() != null) {
			this.getScrollBar().draw(matrixStack, this);
		}

	}

	/**
	 * 
	 */
	@Override
	public boolean mouseClicked(final double xCoord, final double yCoord, final int btn) {
//		this.drag_click.clear();

		// Forward left mouse button down events to the scrollbar
		if (btn == 0 && this.getScrollBar() != null) {
			if (this.getScrollBar().mouseDown(xCoord - this.getGuiLeft(), yCoord - this.getGuiTop())) {
				return true;
			}
		}

		return super.mouseClicked(xCoord, yCoord, btn);
	}

	/**
	 * 
	 */
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		// Forward left mouse button up events to the scrollbar
		if (button == 0 && this.getScrollBar() != null) {
			if (this.getScrollBar().mouseUp(mouseX - this.getGuiLeft(), mouseY - this.getGuiTop())) {
				return true;
			}
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	/**
	 * 
	 */
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		if (this.getScrollBar() != null) {
			// FIXME: Coordinate system of mouseX/mouseY is unclear
			this.getScrollBar().mouseDragged((int) mouseX - this.getGuiLeft(), (int) mouseY - this.getGuiTop());
		}
		return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
	}

	/**
	 * 
	 */
	@Override
	public boolean mouseScrolled(double x, double y, double wheelDelta) {
		if (wheelDelta != 0 && this.getScrollBar() != null) {
			this.getScrollBar().wheel(wheelDelta);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public void tick() {
		super.tick();

		if (this.getScrollBar() != null) {
			this.getScrollBar().tick();
		}
	}

	protected Scrollbar getScrollBar() {
		return this.myScrollBar;
	}

	protected void setScrollBar(final Scrollbar myScrollBar) {
		this.myScrollBar = myScrollBar;
	}

	public void bindTexture(final String base, final String file) {
		final ResourceLocation loc = new ResourceLocation(base, "textures/" + file);
		getMinecraft().getTextureManager().bind(loc);
	}

	public void bindTexture(final String file) {
		final ResourceLocation loc = new ResourceLocation(LegacyVault.MODID, "textures/" + file);
		getMinecraft().getTextureManager().bind(loc);
	}

	public void bindTexture(final ResourceLocation loc) {
		getMinecraft().getTextureManager().bind(loc);
	}
}

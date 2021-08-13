/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Mark Gottschling on May 18, 2021
 *
 */
@Deprecated
public class ScrollableVaultContainerScreen3 extends ContainerScreen<VaultContainer> implements IScrollableContainerScreen {
	
	// this is the resource location for the background image for the GUI
	private static final ResourceLocation TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/scrollable_vault.png");
   
	private boolean scrolling = false;
    private double currentScroll;

	private double mouseSliderDeltaY;
    
	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public ScrollableVaultContainerScreen3(VaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		this.imageHeight = 165;
		this.imageWidth = 193;
	}
	
    @Override
    public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
        super.init(p_init_1_, p_init_2_, p_init_3_);
        // 194 = texture x start (param5)
        // 0 = texture y start (param6)
        // 0 = ?
        // 256, texture x size
        // 256, texture y size
        
        ImageButton scrollButton = new ImageButton(
        		getGuiLeft() + getScrollbarLeftOffset(),	// xpos
        		getGuiTop() + getScrollbarTopOffset(),	// ypos
        		getSliderWidth(),
        		getSliderHeight(), 
        		193, 0, 0, TEXTURE, 256, 256 , button -> {});
        scrollButton.visible = true;
        scrollButton.active = false;
        buttons.add(scrollButton);
    }
    
	@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
		
		// TODO use AppliedEnergistics code here instead. it checks if one the actual slider/button, not the track
		// TODO if using a button, wouldn't there be button evernts to use instead? ie onPress, onDrag, onRelease. could extend ImageButton to implement those.
		// TODO change to a method to get within scrollable bounds
		if (button == 0) {
			Widget slider = buttons.get(0);		
			if (mouseX < slider.x || mouseX > slider.x + getSliderWidth() ||
					mouseY < slider.y || mouseY > slider.y + getSliderHeight()) {
				
			}
			else {
	            setScrolling(true);
	            // capture the delta from mouseY to sliderTopY
	            this.mouseSliderDeltaY = mouseY - slider.y;
			}
		}
//        if(mouseX >= getGuiLeft() + 175 && mouseX <= getGuiLeft() + 175 + getSliderWidth()
//                && mouseY >= getGuiTop() + 18 && mouseY <= getGuiTop() + 124){
//            setScrolling(true);
//            // capture the delta from mouseY to sliderTopY
//            this.mouseSliderDeltaY = mouseY - buttons.get(0).y;
//        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
	
	/**
	 * 
	 */
	@Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0 && isScrolling()) {
			setScrolling(false);
			this.mouseSliderDeltaY = 0;
		}
        return super.mouseReleased(mouseX, mouseY, button);
    }
	
	/**
	 * 
	 */
//    @Override
//    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
//        if(isScrolling()) {
////        	LegacyVault.LOGGER.debug("mouseY -> {}, dragY -> {}", mouseY, dragY);
//        	/*
//        	 *  update button position
//        	 */
//        	// NOTE remember the button.y is (x, 0) of the slider image.
//            double pos = mouseY - mouseSliderDeltaY;
//            double top = this.getGuiTop() + getScrollbarTopOffset();
//            double bottom = top + getScrollbarDistance();
//            // ensure pos is withing the range between top and bottom
//            pos = MathHelper.clamp(pos, top, bottom);
//            buttons.get(0).y = (int) pos;
//            
////            currentScroll = (pos-top)/(bottom-top);
//            // TODO make a convenience method to get size()
////            int val = (int) (currentScroll * (menu.getVaultInventory().getContainerSize()) - getDisplayRowCount()); // NOTE 6 = # of gui inventory rows
//            /*
//             * update inventory container (slots display)
//             */
//            menu.updateContainerInventory(getRowIndex(pos)); // <-- in the container - this sets up the slots for display
//        }
//        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
//    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_mouseScrolled_5_) {
//        currentScroll = (int) (currentScroll - (p_mouseScrolled_5_ / (menu.getInventorySize() / 5)));  // why 5?
//        currentScroll = MathHelper.clamp(currentScroll, this.getGuiTop() + getScrollbarTopOffset(), this.getGuiTop() + 124 - getSliderHeight());
//        buttons.get(0).y = (int) currentScroll;
//        int rowIndex = getRowIndex(currentScroll);
//        menu.updateContainerInventory(rowIndex);
        return false;
    }
    
    /**
     * TODO these row index calculations seem off
     * @param pos
     * @return
     */
    private int getRowIndex(double pos){
    	/*
    	 *  get the scrollable row range. this is the number of rows greater than the gui displayable rows. the mouse y coord
    	 *  will be translated to a value within this range.
    	 *  ex. gui = 3 rows @ 9 columns (27 slots). inventory = 6 rows @ 9 columns (54 slots). there are 3 rows that are
    	 *  not displayed. scrollableRange = 3. the mouse y would translate to [0, 1, 2].
    	 */
        int scrollableRowRange = (menu.getInventorySize() / getDisplayColumnCount()) - getDisplayRowCount();
        LegacyVault.LOGGER.debug("total scrollable rows -> {}", scrollableRowRange);
        
        // calculate total scrollable distance
        double topY = this.getGuiTop() + getScrollbarTopOffset();
//        double bottomY = topY + getScrollbarHeight() - getSliderHeight();
//        double scrollbarDistance = (bottomY - topY);
        
        // calculate slider's position in scrollbar
        double relativeSliderPos = (pos - topY);
        
        // calculate position as a percent of total distance
        double distanceScrolled = relativeSliderPos / getScrollbarDistance();
        LegacyVault.LOGGER.debug("rowIndex -> {}", (int) Math.floor(distanceScrolled * (scrollableRowRange - 1)));
        // convert to row index
       return (int) Math.floor(distanceScrolled * (scrollableRowRange));

    }

	@Override
	public int getSliderHeight() {
		return 15;
	}

	@Override
	public int getSliderWidth() {
		return 12;
	}
	
	@Override
	public int getScrollbarHeight() {
		return 52;
	}

	public int getScrollbarDistance() {
		return 37;
	}
	
	@Override
    public int getScrollbarTopOffset() {
		return 18;
	}
	
	@Override
    public int getScrollbarLeftOffset() {
		return 174;
	}

	@Override
	public int getDisplayColumnCount() {
		return menu.getContainerInventoryColumnCount();
	}

	// TODO move to Container
	@Override
	public int getDisplayRowCount() {
		return menu.getContainerInventoryRowCount();
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
		this.minecraft.getTextureManager().bind(TEXTURE);

		// width and height are the size provided to the window when initialised after creation.
		// xSize, ySize are the expected size of the BG_TEXTURE-? usually seems to be left as a default.
		// The code below is typical for vanilla containers, so I've just copied that- it appears to centre the BG_TEXTURE within
		//  the available window
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}

	public boolean isScrolling() {
		return scrolling;
	}

	public void setScrolling(boolean scrolling) {
		this.scrolling = scrolling;
	}

	public double getCurrentScroll() {
		return currentScroll;
	}

	public void setCurrentScroll(double currentScroll) {
		this.currentScroll = currentScroll;
	}
}

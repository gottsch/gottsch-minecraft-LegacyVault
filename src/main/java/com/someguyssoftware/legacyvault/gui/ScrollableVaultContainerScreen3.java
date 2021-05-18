/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.VaultContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Mark Gottschling on May 18, 2021
 *
 */
public class ScrollableVaultContainerScreen3 extends ContainerScreen<VaultContainer> {
	
	// this is the resource location for the background image for the GUI
	private static final ResourceLocation TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault.png");
   
	private boolean scrolling = false;
    private double currentScroll;
    
	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public ScrollableVaultContainerScreen3(VaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		// TODO update these to match new GUI BG minus the slider area
		this.imageHeight = 136;
		this.imageWidth = 195;
	}
	
    @Override
    public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
        super.init(p_init_1_, p_init_2_, p_init_3_);
        // gui left + 175 = button x position
        // gui top + 18 = button y position
        // 12 = button width
        // 15 = button height
        // 194 = texture x start (param5)
        // 0 = texture y start (param6)
        // 512, texture x size
        // 512, texture y size
        
        ImageButton scrollButton = new ImageButton(getGuiLeft() + 175, getGuiTop() + 18, 12, 15, 194, 0, 0, TEXTURE, 512, 512 , button -> {});
        scrollButton.visible = true;
        scrollButton.active = false;
        buttons.add(scrollButton);
    }
    
	@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// TODO use AppliedEnergistics code here instead. it checks if one the actual slider/button, not the track
		// TODO if using a button, wouldn't there be button evernts to use instead? ie onPress, onDrag, onRelease. could extend ImageButton to implement those.
		// TODO change to a method to get within scrollable bounds
        if(mouseX >= getGuiLeft() + 175 && mouseX <= getGuiLeft() + 175 + 12
                && mouseY >= getGuiTop() + 18 && mouseY <= getGuiTop() + 124){
            setScrolling(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
	
	/**
	 * 
	 */
	@Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0 && isScrolling()) {
			setScrolling(false);
		}
        return super.mouseReleased(mouseX, mouseY, button);
    }
	
	/**
	 * 
	 */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(isScrolling()){
            double pos = mouseY;
            double top = this.getGuiTop() + 18;
            double bottom = this.getGuiTop() + 124 - 15;
            pos = MathHelper.clamp(pos, top, bottom);
            buttons.get(0).y = (int) pos;
            currentScroll = (pos-top)/(bottom-top);
            // TODO make a convenience method to get size()
            int val = (int) (currentScroll * (menu.getVaultInventory().getContainerSize())-6); // NOTE 6 = # of gui inventory rows
            menu.updateSlots(val); // <-- in the container - this sets up the slots for display
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        currentScroll = (int) (currentScroll - (p_mouseScrolled_5_ / (menu.getInventorySize()/5)));
        currentScroll = MathHelper.clamp(currentScroll, this.getGuiTop() + 18, this.getGuiTop() + 124 - 15);
        buttons.get(0).y = (int) currentScroll;
        container.updateSlots(map(currentScroll));
        return false;
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
	protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
		// TODO Auto-generated method stub

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

/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.inventory.CommunityVaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.SortVaultPacket;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Mark Gottschling on 2/21/2025
 */
public class CommunityVaultScreen extends VaultScreen {

    private static final ResourceLocation BG_TEXTURE =
            new ResourceLocation(LegacyVault.MOD_ID, "textures/gui/container/personal_vault.png");

    // Scrollbar geometry (panel-relative)
    private static final int SCROLLBAR_X = 172;
    private static final int SCROLLBAR_Y = 42;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = CommunityVaultContainerMenu.VISIBLE_ROWS * 18 - 2; // 90
    private static final int THUMB_HEIGHT = 15;

    // UV coordinates of the scrollbar thumb sprite within the texture sheet
    private static final int THUMB_U = 232;
    private static final int THUMB_V = 0;

    // UV coordinates of the locked-slot overlay sprite within the texture sheet
    private static final int LOCKED_SLOT_U = 196;
    private static final int LOCKED_SLOT_V = 0;

    private float scrollOffs = 0.0f;
    private boolean isDraggingScrollBar = false;

    private EditBox searchBox;
    private final Set<Integer> dimmedSlots = new HashSet<>();

    public CommunityVaultScreen(VaultContainerMenu containerMenu, Inventory inventory, Component name) {
        super(containerMenu, inventory, name);
        imageWidth = 195;
        imageHeight = 238;
    }

    @Override
    protected void init() {
        super.init();
        // Search box: panel x=8, y=18 → screen-absolute
        searchBox = new EditBox(this.font, leftPos + 8, topPos + 18, 142, 20, Component.empty());
        searchBox.setMaxLength(50);
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);

        // Sort button: immediately right of search box
        addRenderableWidget(Button.builder(Component.literal("Az"), b -> sendSortRequest())
                .pos(leftPos + 152, topPos + 18)
                .size(20, 20)
                .build());
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        graphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Scrollbar thumb
        if (isScrollBarActive()) {
            int thumbTop = getThumbTop();
            graphics.blit(BG_TEXTURE, leftPos + SCROLLBAR_X, thumbTop, THUMB_U, THUMB_V, SCROLLBAR_WIDTH, THUMB_HEIGHT);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        final int LABEL_X = 8;
        gui.drawString(this.font,
                Component.translatable(LangUtil.screen("community_vault.name")).getString(),
                LABEL_X, 6, Color.DARK_GRAY.getRGB(), false);

        gui.drawString(this.font, this.playerInventoryTitle,
                LABEL_X, communityMenu().getPlayerInventoryYPos() - 12,
                Color.DARK_GRAY.getRGB(), false);
    }

    @Override
    protected void renderOverlays(GuiGraphics graphics, int mouseX, int mouseY) {
        int firstSlot = communityMenu().getContainerFirstSlotIndex();
        int activeSlots = communityMenu().getVaultTier() * 9;
        int totalSlots = communityMenu().getMaxTier() * 9;

        for (int i = 0; i < totalSlots; i++) {
            Slot slot = getMenu().slots.get(firstSlot + i);
            if (slot.y < 0) continue;  // off-viewport
            int invSlot = slot.getSlotIndex();

            if (invSlot >= activeSlots) {
                graphics.blit(BG_TEXTURE,
                        leftPos + slot.x - 1, topPos + slot.y - 1,
                        LOCKED_SLOT_U, LOCKED_SLOT_V, 18, 18);
            } else if (dimmedSlots.contains(invSlot)) {
                graphics.fill(leftPos + slot.x, topPos + slot.y,
                        leftPos + slot.x + 16, topPos + slot.y + 16,
                        0xA0101010);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Mouse / keyboard input
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isScrollBarActive()) {
            int thumbTop = getThumbTop();
            if (mouseX >= leftPos + SCROLLBAR_X && mouseX < leftPos + SCROLLBAR_X + SCROLLBAR_WIDTH
                    && mouseY >= thumbTop && mouseY < thumbTop + THUMB_HEIGHT) {
                isDraggingScrollBar = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollBar) {
            int trackTop = topPos + SCROLLBAR_Y;
            int trackRange = SCROLLBAR_HEIGHT - THUMB_HEIGHT;
            scrollOffs = (float) ((mouseY - trackTop - THUMB_HEIGHT / 2.0) / trackRange);
            scrollOffs = Mth.clamp(scrollOffs, 0.0f, 1.0f);
            applyScroll();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDraggingScrollBar) {
            isDraggingScrollBar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isScrollBarActive()) return false;
        int scrollableRows = communityMenu().getMaxTier() - CommunityVaultContainerMenu.VISIBLE_ROWS;
        scrollOffs = Mth.clamp(scrollOffs - (float) (delta / scrollableRows), 0.0f, 1.0f);
        applyScroll();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC must always reach the screen so the player can close the GUI,
        // even while the search box has keyboard focus.
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (searchBox.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (searchBox.isFocused() && searchBox.isVisible()) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (searchBox.charTyped(c, modifiers)) return true;
        return super.charTyped(c, modifiers);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean isScrollBarActive() {
        return communityMenu().getMaxTier() > CommunityVaultContainerMenu.VISIBLE_ROWS;
    }

    private int getThumbTop() {
        int trackRange = SCROLLBAR_HEIGHT - THUMB_HEIGHT;
        return topPos + SCROLLBAR_Y + (int) (scrollOffs * trackRange);
    }

    private void applyScroll() {
        int scrollableRows = communityMenu().getMaxTier() - CommunityVaultContainerMenu.VISIBLE_ROWS;
        int firstRow = Math.round(scrollOffs * scrollableRows);
        communityMenu().scrollTo(firstRow);
    }

    private void onSearchChanged(String query) {
        dimmedSlots.clear();
        if (query.isEmpty()) return;
        String lower = query.toLowerCase(Locale.ROOT);
        // Walk the full backing inventory (not just the visible viewport)
        int total = communityMenu().getMaxTier() * 9;
        for (int invSlot = 0; invSlot < total; invSlot++) {
            ItemStack stack = communityMenu().getVaultInventory().getStackInSlot(invSlot);
            if (!stack.isEmpty()) {
                String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
                if (!name.contains(lower)) {
                    dimmedSlots.add(invSlot);
                }
            }
        }
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>(super.getTooltipFromContainerItem(stack));
        if (hoveredSlot != null
                && hoveredSlot.index >= communityMenu().getContainerFirstSlotIndex()
                && !stack.isEmpty()) {
            int total = countItemTotal(stack);
            if (total > stack.getCount()) {
                tooltip.add(Component.literal("× " + total + " in vault")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        return tooltip;
    }

    private int countItemTotal(ItemStack target) {
        int total = 0;
        int activeSlots = communityMenu().getVaultTier() * 9;
        for (int i = 0; i < activeSlots; i++) {
            ItemStack s = communityMenu().getVaultInventory().getStackInSlot(i);
            if (!s.isEmpty() && ItemStack.isSameItemSameTags(target, s)) {
                total += s.getCount();
            }
        }
        return total;
    }

    private void sendSortRequest() {
        LegacyVaultNetworking.channel.sendToServer(new SortVaultPacket());
    }

    private CommunityVaultContainerMenu communityMenu() {
        return (CommunityVaultContainerMenu) getMenu();
    }
}

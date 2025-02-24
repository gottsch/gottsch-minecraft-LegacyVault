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

import com.mojang.blaze3d.vertex.PoseStack;
import mod.gottsch.forge.legacyvault.core.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

/**
 * @author by Mark Gottschling on 2/21/2025
 */
public class CommunityVaultScreen extends VaultScreen {
    /**
     * @param containerMenu
     * @param inventory
     * @param name
     */
    public CommunityVaultScreen(VaultContainerMenu containerMenu, Inventory inventory, Component name) {
        super(containerMenu, inventory, name);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        final int LABEL_XPOS = 8;
        final int FONT_Y_SPACING = 12;
        final int CHEST_LABEL_YPOS = getMenu().getTitleYPos() - FONT_Y_SPACING;

        this.font.draw(matrixStack, Component.translatable(LangUtil.screen("community_vault.name")).getString(), LABEL_XPOS, getMenu().getVaultsRemainingYPos(),Color.darkGray.getRGB());
    }
}

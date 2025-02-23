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

import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

/**
 * @author by Mark Gottschling on 2/21/2025
 */
public class PersonalVaultScreen extends VaultScreen {
    /**
     * @param containerMenu
     * @param inventory
     * @param name
     */
    public PersonalVaultScreen(VaultContainerMenu containerMenu, Inventory inventory, Component name) {
        super(containerMenu, inventory, name);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        final int LABEL_XPOS = 8;
        final int FONT_Y_SPACING = 12;
        final int CHEST_LABEL_YPOS = getMenu().getTitleYPos() - FONT_Y_SPACING;

        gui.drawString(this.font, Component.translatable(LangUtil.screen("personal_vault.name")).getString(), LABEL_XPOS, CHEST_LABEL_YPOS, Color.DARK_GRAY.getRGB(), false);

        String vaultsRemaining = "";

        if (Config.ServerConfig.PERSONAL.enabled.get()){
            // check for unlimited
            if (!Config.ServerConfig.PERSONAL.unlimitedVaults.get()) {
                IPlayerVaultsHandler cap = getInventory().player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
                    return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
                });
                vaultsRemaining = Component.translatable(LangUtil.screen("vaults_remaining"), String.valueOf(Config.ServerConfig.PERSONAL.vaultsPerPlayer.get() - cap.getCount()), Config.ServerConfig.PERSONAL.vaultsPerPlayer.get()).getString();
            }
            else {
                vaultsRemaining = Component.translatable(LangUtil.screen("unlimited_vaults")).getString();
            }
        }
        gui.drawString(this.font, vaultsRemaining, LABEL_XPOS, getMenu().getVaultsRemainingYPos(), Color.DARK_GRAY.getRGB(), false);
    }
}

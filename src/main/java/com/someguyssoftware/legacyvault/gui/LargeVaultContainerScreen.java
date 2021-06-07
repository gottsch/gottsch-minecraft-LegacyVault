/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.legacyvault.gui;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.inventory.AbstractLegacyVaultContainer;
import com.someguyssoftware.legacyvault.inventory.LargeVaultContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class LargeVaultContainerScreen extends AbstractVaultContainerScreen<LargeVaultContainer> {

	// this is the resource location for the background image for the GUI
	private static final ResourceLocation BG_TEXTURE = new ResourceLocation(LegacyVault.MODID, "textures/gui/container/vault3c.png");

	/**
	 * 
	 * @param screenContainer
	 * @param playerInventory
	 * @param title
	 */
	public LargeVaultContainerScreen(LargeVaultContainer screenContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(screenContainer, playerInventory, title);
		imageWidth = 247;
		imageHeight = 246;
	}

	public ResourceLocation getBgTexture() {
		return BG_TEXTURE;
	}
}

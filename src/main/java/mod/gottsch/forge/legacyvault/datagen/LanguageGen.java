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
package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * 
 * @author Mark Gottschling on Feb 2, 2025
 *
 */
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput gen, String locale) {
        super(gen, LegacyVault.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {

        // item group

        // items
        add(ModItems.CONTRACT.get(), "Legacy Vault Contract");
        add(ModItems.APPLICATION.get(), "Legacy Vault Application");

        // blocks
        add(ModBlocks.RUSTIC_VAULT.get(), "Rustic Legacy Vault");
        add(ModBlocks.CLASSIC_VAULT.get(), "Classic Legacy Vault");
        add(ModBlocks.COMMUNITY_VAULT.get(), "Community Legacy Vault");
        
        /*
         *  Util.tooltips
         */
        // general
        add(LangUtil.tooltip("hold_shift"), "Hold [SHIFT] to expand");

        add(LangUtil.tooltip("usage.personal_vault"), "A Personal Vault provides a persistent multi-world inventory~that only the owning player can access.");
        add(LangUtil.tooltip("usage.community_vault"), "A Community Vault provides a persistent multi-world inventory~that all players can use, but can only access their own inventory.~A Community Vault can only be placed or destroy by players~with the correct privileges, ex. creative mode.");

        /*
         * Screen
         */
        add(LangUtil.screen("vault.name"),"Legacy Vault");
        add(LangUtil.screen("personal_vault.name"), "Personal Legacy Vault");
        add(LangUtil.screen("community_vault.name"), "Community Legacy Vault");
        add(LangUtil.screen("unlimited_vaults"), "Vaults Remaining: Unlimited");
        add(LangUtil.screen("vaults_remaining"), "Vaults Remaining: %s of %s");
        add(LangUtil.screen("community_vault"), "Community Legacy Vault");
    }
}

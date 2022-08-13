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
package mod.gottsch.forge.legacyvault.recipe.condition;

import com.google.gson.JsonObject;

import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * @author Mark Gottschling on May 26, 2021
 *
 */
public class VaultEasyDifficultyCondition implements ICondition {
	public static final VaultEasyDifficultyCondition INSTANCE = new VaultEasyDifficultyCondition();
    private static final ResourceLocation NAME = new ResourceLocation("legacyvault", "vault_easy_difficulty");
    
	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return !ServerConfig.PUBLIC_VAULT.enablePublicVault.get() && ServerConfig.GENERAL.recipeDifficulty.get().equalsIgnoreCase("easy");
	}

    @Override
    public String toString() {
        return "easy";
    }
    
    public static class Serializer implements IConditionSerializer<VaultEasyDifficultyCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, VaultEasyDifficultyCondition value) { }

        @Override
        public VaultEasyDifficultyCondition read(JsonObject json) {
            return VaultEasyDifficultyCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return VaultEasyDifficultyCondition.NAME;
        }
    }
}
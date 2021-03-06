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
package com.someguyssoftware.legacyvault.recipe.condition;

import com.google.gson.JsonObject;
import com.someguyssoftware.legacyvault.config.Config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * @author Mark Gottschling on May 26, 2021
 *
 */
public class VaultNormalDifficultyCondition implements ICondition {
	public static final VaultNormalDifficultyCondition INSTANCE = new VaultNormalDifficultyCondition();
    private static final ResourceLocation NAME = new ResourceLocation("legacyvault", "vault_normal_difficulty");
    
	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return !Config.PUBLIC_VAULT.enablePublicVault.get() && Config.GENERAL.recipeDifficulty.get().equalsIgnoreCase("normal");
	}

    @Override
    public String toString() {
        return "normal";
    }
    
    public static class Serializer implements IConditionSerializer<VaultNormalDifficultyCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, VaultNormalDifficultyCondition value) { }

        @Override
        public VaultNormalDifficultyCondition read(JsonObject json) {
            return VaultNormalDifficultyCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return VaultNormalDifficultyCondition.NAME;
        }
    }
}

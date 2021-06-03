/**
 * 
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
public class VaultEasyDifficultyCondition implements ICondition {
	public static final VaultEasyDifficultyCondition INSTANCE = new VaultEasyDifficultyCondition();
    private static final ResourceLocation NAME = new ResourceLocation("legacyvault", "vault_easy_difficulty");
    
	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return !Config.GENERAL.enablePublicVault.get() && Config.GENERAL.recipeDifficulty.get().equalsIgnoreCase("easy");
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

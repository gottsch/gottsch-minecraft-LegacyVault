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
public class VaultNormalDifficultyCondition implements ICondition {
	public static final VaultNormalDifficultyCondition INSTANCE = new VaultNormalDifficultyCondition();
    private static final ResourceLocation NAME = new ResourceLocation("legacyvault", "vault_normal_difficulty");
    
	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return !Config.GENERAL.enablePublicVault.get() && Config.GENERAL.recipeDifficulty.get().equalsIgnoreCase("normal");
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

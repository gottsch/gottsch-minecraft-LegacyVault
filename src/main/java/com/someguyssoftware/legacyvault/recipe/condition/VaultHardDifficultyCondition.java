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
public class VaultHardDifficultyCondition implements ICondition {
	public static final VaultHardDifficultyCondition INSTANCE = new VaultHardDifficultyCondition();
    private static final ResourceLocation NAME = new ResourceLocation("legacyvault", "vault_hard_difficulty");
    
	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return !Config.GENERAL.enablePublicVault.get() && Config.GENERAL.recipeDifficulty.get().equalsIgnoreCase("hard");
	}

    @Override
    public String toString() {
        return "hard";
    }
    
    public static class Serializer implements IConditionSerializer<VaultHardDifficultyCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, VaultHardDifficultyCondition value) { }

        @Override
        public VaultHardDifficultyCondition read(JsonObject json) {
            return VaultHardDifficultyCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return VaultHardDifficultyCondition.NAME;
        }
    }
}

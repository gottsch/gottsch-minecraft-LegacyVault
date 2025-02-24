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


import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultEasyDifficultyCondition;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultHardDifficultyCondition;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultNormalDifficultyCondition;
import mod.gottsch.forge.legacyvault.core.tags.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

/**
 * @author Mark Gottschling Feb 2, 2025
 */
public class Recipes extends RecipeProvider implements IConditionBuilder {

	public Recipes(PackOutput generator) {
		super(generator);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> recipe) {

		// contract
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CONTRACT.get())
				.pattern("pip")
				.pattern("pip")
				.pattern("pip")
				.define('p', Items.PAPER)
				.define('i', Items.INK_SAC)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.PAPER, Items.INK_SAC))
				.save(recipe);

		// rustic vault
		ConditionalRecipe.builder()
				// Add the conditions for the recipe
				.addCondition(VaultNormalDifficultyCondition.INSTANCE)
//					not(
//						tagEmpty(ModTags.Items.NORMAL_RECIPE)
//					)
//				)
				.addRecipe(rustic -> {
					ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
							.pattern(	"iiv")
							.pattern("ici")
							.pattern("iii")
							.define('i', Items.IRON_INGOT)
							.define('v', ModTags.Items.VAULT_CONTRACTS)
							.define('c', Items.CHEST)
							.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
									Items.CHEST, Items.IRON_INGOT, ModItems.CONTRACT.get()))
							.save(rustic);
				})
//				.addCondition(not(tagEmpty(ModTags.Items.EASY_RECIPE)))
				.addCondition(VaultEasyDifficultyCondition.INSTANCE)
				.addRecipe(this::buildEasyDifficultyVaultRecipe)
//				.addCondition(not(tagEmpty(ModTags.Items.HARD_RECIPE)))
				.addCondition(VaultHardDifficultyCondition.INSTANCE)
				.addRecipe(this::buildHardDifficultyVaultRecipe)
				.build(recipe, ModBlocks.RUSTIC_VAULT.getId());

		// classic vault
		ConditionalRecipe.builder()
				// Add the conditions for the recipe
//				.addCondition(not(tagEmpty(ModTags.Items.NORMAL_RECIPE)))
				.addCondition(VaultNormalDifficultyCondition.INSTANCE)
				.addRecipe(classic -> {
					ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CLASSIC_VAULT.get())
							.pattern("iiv")
							.pattern("ici")
							.pattern("iii")
							.define('i', Items.IRON_INGOT)
							.define('v', ModTags.Items.VAULT_CONTRACTS)
							.define('c', Items.BARREL)
							.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
									Items.BARREL, Items.IRON_INGOT, ModItems.CONTRACT.get()))
							.save(classic);
				})
//				.addCondition(not(tagEmpty(ModTags.Items.EASY_RECIPE)))
				.addCondition(VaultEasyDifficultyCondition.INSTANCE)
				.addRecipe(classic -> {
					ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CLASSIC_VAULT.get())
						.pattern("  v")
						.pattern(" c ")
						.pattern("   ")
						.define('v', ModTags.Items.VAULT_CONTRACTS)
						.define('c', Items.BARREL)
						.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
								Items.BARREL, ModItems.CONTRACT.get()))
						.save(classic);
				})
//				.addCondition(not(tagEmpty(ModTags.Items.HARD_RECIPE)))
				.addCondition(VaultHardDifficultyCondition.INSTANCE)
				.addRecipe(classic -> {
					ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CLASSIC_VAULT.get())
							.pattern("iiv")
							.pattern("ici")
							.pattern("iii")
							.define('i', Items.IRON_BLOCK)
							.define('v', ModTags.Items.VAULT_CONTRACTS)
							.define('c', Items.BARREL)
							.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
									Items.BARREL, Items.IRON_BLOCK, ModItems.CONTRACT.get()))
							.save(classic);
				})
				.build(recipe, ModBlocks.CLASSIC_VAULT.getId());
	}

	protected void buildNormalDifficultyVaultRecipe(Consumer<FinishedRecipe> recipe) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', Items.IRON_INGOT)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, Items.IRON_INGOT, ModItems.CONTRACT.get()))
				.save(recipe);
	}

	protected void buildEasyDifficultyVaultRecipe(Consumer<FinishedRecipe> recipe) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern("  v")
				.pattern(" c ")
				.pattern("   ")
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, ModItems.CONTRACT.get()))
				.save(recipe);
	}

	protected void buildHardDifficultyVaultRecipe(Consumer<FinishedRecipe> recipe) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', Items.IRON_BLOCK)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, Items.IRON_BLOCK, ModItems.CONTRACT.get()))
				.save(recipe);
	}
}

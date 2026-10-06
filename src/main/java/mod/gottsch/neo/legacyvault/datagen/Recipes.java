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
package mod.gottsch.neo.legacyvault.datagen;


import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.block.ModBlocks;
import mod.gottsch.neo.legacyvault.core.item.ModItems;
import mod.gottsch.neo.legacyvault.core.recipe.condition.VaultEasyTierCondition;
import mod.gottsch.neo.legacyvault.core.recipe.condition.VaultHardTierCondition;
import mod.gottsch.neo.legacyvault.core.recipe.condition.VaultNormalTierCondition;
import mod.gottsch.neo.legacyvault.core.tags.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * NeoForge 1.21 has no multi-alternative ConditionalRecipe: a condition gates a whole recipe file.
 * So each vault gets one file per tier. The 1.20.1 ConditionalRecipe used the first alternative whose
 * condition passed (order: normal, easy, hard); the NOT conditions below keep that precedence, so only
 * one recipe per vault loads even if a datapack puts the vault in several tier tags.
 * The normal-tier file keeps the original recipe id (e.g. legacyvault:rustic_vault).
 *
 * @author Mark Gottschling Feb 2, 2025
 */
public class Recipes extends RecipeProvider implements IConditionBuilder {

	public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	private ICondition[] normalTier() {
		return new ICondition[] {VaultNormalTierCondition.INSTANCE};
	}

	private ICondition[] easyTier() {
		return new ICondition[] {VaultEasyTierCondition.INSTANCE, not(VaultNormalTierCondition.INSTANCE)};
	}

	private ICondition[] hardTier() {
		return new ICondition[] {VaultHardTierCondition.INSTANCE, not(VaultNormalTierCondition.INSTANCE), not(VaultEasyTierCondition.INSTANCE)};
	}

	private static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(LegacyVault.MOD_ID, path);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipe) {

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
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern(	"iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', ModTags.Items.NORMAL_RECIPE_BASE_MATERIALS)// Items.IRON_INGOT)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, Items.IRON_INGOT, ModItems.CONTRACT.get()))
				.save(recipe.withConditions(normalTier()), id("rustic_vault"));
		buildEasyTierVaultRecipe(recipe.withConditions(easyTier()));
		buildHardTierVaultRecipe(recipe.withConditions(hardTier()));

		// classic vault
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CLASSIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', ModTags.Items.NORMAL_RECIPE_BASE_MATERIALS)//Items.IRON_INGOT)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.BARREL)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.BARREL, Items.IRON_INGOT, ModItems.CONTRACT.get()))
				.save(recipe.withConditions(normalTier()), id("classic_vault"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CLASSIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', ModTags.Items.EASY_RECIPE_BASE_MATERIALS)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.BARREL)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.BARREL, ModItems.CONTRACT.get()))
				.save(recipe.withConditions(easyTier()), id("classic_vault_easy"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CLASSIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', ModTags.Items.HARD_RECIPE_BASE_MATERIALS)//Items.IRON_BLOCK)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.BARREL)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.BARREL, Items.IRON_BLOCK, ModItems.CONTRACT.get()))
				.save(recipe.withConditions(hardTier()), id("classic_vault_hard"));
	}

	protected void buildNormalTierVaultRecipe(RecipeOutput recipe) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', Items.IRON_INGOT)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, Items.IRON_INGOT, ModItems.CONTRACT.get()))
				.save(recipe, id("rustic_vault"));
	}

	protected void buildEasyTierVaultRecipe(RecipeOutput recipe) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', ModTags.Items.EASY_RECIPE_BASE_MATERIALS)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, ModItems.CONTRACT.get()))
				.save(recipe, id("rustic_vault_easy"));
	}

	protected void buildHardTierVaultRecipe(RecipeOutput recipe) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RUSTIC_VAULT.get())
				.pattern("iiv")
				.pattern("ici")
				.pattern("iii")
				.define('i', ModTags.Items.HARD_RECIPE_BASE_MATERIALS) //Items.IRON_BLOCK)
				.define('v', ModTags.Items.VAULT_CONTRACTS)
				.define('c', Items.CHEST)
				.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(
						Items.CHEST, Items.IRON_BLOCK, ModItems.CONTRACT.get()))
				.save(recipe, id("rustic_vault_hard"));
	}
}

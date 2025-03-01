/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.recipe;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultEasyDifficultyCondition;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultHardDifficultyCondition;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultNormalDifficultyCondition;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author Mark Gottschling on May 26, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class LegacyVaultRecipes {

	@SubscribeEvent
	public static void registerRecipeSerialziers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		LegacyVault.LOGGER.info("in recipe subscribe event");
		CraftingHelper.register(VaultEasyDifficultyCondition.Serializer.INSTANCE);
		CraftingHelper.register(VaultNormalDifficultyCondition.Serializer.INSTANCE);
		CraftingHelper.register(VaultHardDifficultyCondition.Serializer.INSTANCE);
	}
}

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
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultEasyTierCondition;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultHardTierCondition;
import mod.gottsch.forge.legacyvault.core.recipe.condition.VaultNormalTierCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * @author Mark Gottschling on May 26, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class LegacyVaultRecipes {

	@SubscribeEvent
	public static void registerRecipeSerialziers(RegisterEvent event) {
		if (event.getRegistryKey() == ForgeRegistries.Keys.RECIPE_SERIALIZERS) {
			LegacyVault.LOGGER.info("in recipe subscribe event");
			event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
					helper -> CraftingHelper.register(VaultEasyTierCondition.Serializer.INSTANCE)
					);
			event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
					helper -> CraftingHelper.register(VaultNormalTierCondition.Serializer.INSTANCE)
					);
			event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
					helper -> CraftingHelper.register(VaultHardTierCondition.Serializer.INSTANCE)
					);
		}
	}
}

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
package mod.gottsch.neo.legacyvault.core.recipe;

import com.mojang.serialization.MapCodec;
import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.recipe.condition.VaultEasyTierCondition;
import mod.gottsch.neo.legacyvault.core.recipe.condition.VaultHardTierCondition;
import mod.gottsch.neo.legacyvault.core.recipe.condition.VaultNormalTierCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers the recipe tier condition codecs (json "type": "legacyvault:vault_*_tier").
 */
public class LegacyVaultRecipes {
	private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
			DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, LegacyVault.MOD_ID);

	static {
		CONDITION_CODECS.register("vault_easy_tier", () -> VaultEasyTierCondition.CODEC);
		CONDITION_CODECS.register("vault_normal_tier", () -> VaultNormalTierCondition.CODEC);
		CONDITION_CODECS.register("vault_hard_tier", () -> VaultHardTierCondition.CODEC);
	}

	public static void register(IEventBus eventBus) {
		CONDITION_CODECS.register(eventBus);
	}
}

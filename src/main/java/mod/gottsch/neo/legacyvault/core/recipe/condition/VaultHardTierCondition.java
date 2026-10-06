/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.core.recipe.condition;

import java.util.Collection;

import com.mojang.serialization.MapCodec;

import mod.gottsch.neo.legacyvault.core.item.ModItems;
import mod.gottsch.neo.legacyvault.core.tags.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * @author Mark Gottschling on May 26, 2021
 *
 */
public class VaultHardTierCondition implements ICondition {
	public static final VaultHardTierCondition INSTANCE = new VaultHardTierCondition();
	// registered as legacyvault:vault_hard_tier in LegacyVaultRecipes
	public static final MapCodec<VaultHardTierCondition> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(IContext context) {
		Collection<Holder<Item>> items = context.getTag(ModTags.Items.HARD_RECIPE);
        for(Holder<Item> holder : items) {
            if (holder.value() == ModItems.CLASSIC_VAULT.get()) {
                return true;
            }
        }
        return false;
	}

    @Override
    public String toString() {
        return "hard";
    }

}

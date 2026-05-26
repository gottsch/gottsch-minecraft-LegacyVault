/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Adds a Vault Upgrade item to the loot pool for matching loot tables, based on
 * server config (enable flag, drop chance, count, and the list of loot table IDs).
 *
 * All filtering is done in {@link #doApply(ObjectArrayList, LootContext)} from config;
 * the modifier itself fires on every loot roll, so a single registered instance
 * covers every supported table.
 *
 * @author Mark Gottschling on May 24, 2026
 */
public class VaultUpgradeLootModifier extends LootModifier {

    public static final Codec<VaultUpgradeLootModifier> CODEC = RecordCodecBuilder.create(inst ->
            LootModifier.codecStart(inst).apply(inst, VaultUpgradeLootModifier::new));

    public VaultUpgradeLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.ServerConfig.GENERAL.vaultUpgradeLootEnabled.get()) {
            return generatedLoot;
        }

        ResourceLocation tableId = context.getQueriedLootTableId();
        if (tableId == null || !UpgradeLootTablesLoader.contains(tableId.toString())) {
            return generatedLoot;
        }

        double chance = Config.ServerConfig.GENERAL.vaultUpgradeLootChance.get();
        if (context.getRandom().nextDouble() >= chance) {
            return generatedLoot;
        }

        int count = Config.ServerConfig.GENERAL.vaultUpgradeLootCount.get();
        generatedLoot.add(new ItemStack(ModItems.VAULT_UPGRADE.get(), count));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}

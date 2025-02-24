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
package mod.gottsch.forge.legacyvault.core.item;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Mark Gottschling on May 3, 2021
 *
 */
public class ModItems {
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CreativeModeTab.TAB_MISC);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LegacyVault.MOD_ID);

    public static final RegistryObject<Item> RUSTIC_VAULT = fromBlock(ModBlocks.RUSTIC_VAULT);
    public static final RegistryObject<Item> CLASSIC_VAULT = fromBlock(ModBlocks.CLASSIC_VAULT);
    public static final RegistryObject<Item> COMMUNITY_VAULT = fromBlock(ModBlocks.COMMUNITY_VAULT);

    public static final RegistryObject<Item> CONTRACT = ITEMS.register("vault_contract", () -> new Item(ITEM_PROPERTIES));

    @Deprecated
    public static final RegistryObject<Item> APPLICATION = ITEMS.register("vault_application", () -> new Item(ITEM_PROPERTIES));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    /*
     * author: McJty
     *  conveniance method: take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
     */
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new VaultBlockItem(block.get(), ITEM_PROPERTIES));
    }
}


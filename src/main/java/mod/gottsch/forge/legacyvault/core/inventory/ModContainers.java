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
package mod.gottsch.forge.legacyvault.core.inventory;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Mark Gottschling on 2/18/2025
 */
public class ModContainers {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, LegacyVault.MOD_ID);

    // containers
    public static final RegistryObject<MenuType<VaultContainerMenu>> COMMUNITY_VAULT_CONTAINER = MENUS.register("community_vault",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new CommunityVaultContainerMenu(windowId, data.readBlockPos(), inventory, inventory.player)));

    public static final RegistryObject<MenuType<VaultContainerMenu>> PERSONAL_VAULT_CONTAINER = MENUS.register("personal_vault",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new PersonalVaultContainerMenu(windowId, data.readBlockPos(), inventory, inventory.player)));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

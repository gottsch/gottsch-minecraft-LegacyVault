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
package mod.gottsch.forge.legacyvault.core.block.entity;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Mark Gottschling on 2/17/2025
 */
public class ModBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, LegacyVault.MOD_ID);

    public static final RegistryObject<BlockEntityType<RusticVaultBlockEntity>> RUSTIC_VAULT =
            BLOCK_ENTITIES.register("rustic_vault", () -> BlockEntityType.Builder.of(RusticVaultBlockEntity::new, ModBlocks.RUSTIC_VAULT.get()).build(null));

    public static final RegistryObject<BlockEntityType<ClassicVaultBlockEntity>> CLASSIC_VAULT =
            BLOCK_ENTITIES.register("classic_vault", () -> BlockEntityType.Builder.of(ClassicVaultBlockEntity::new, ModBlocks.CLASSIC_VAULT.get()).build(null));

    public static final RegistryObject<BlockEntityType<CommunityVaultBlockEntity>> COMMUNITY_VAULT =
            BLOCK_ENTITIES.register("community_vault", () -> BlockEntityType.Builder.of(CommunityVaultBlockEntity::new, ModBlocks.COMMUNITY_VAULT.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

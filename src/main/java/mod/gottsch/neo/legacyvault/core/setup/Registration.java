/*
 * This file is part of  Treasure2.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.core.setup;

import com.mojang.serialization.MapCodec;
import mod.gottsch.neo.legacyvault.core.LegacyVault;
import mod.gottsch.neo.legacyvault.core.block.ModBlocks;
import mod.gottsch.neo.legacyvault.core.block.entity.ModBlockEntities;
import mod.gottsch.neo.legacyvault.core.capability.ModAttachments;
import mod.gottsch.neo.legacyvault.core.inventory.ModContainers;
import mod.gottsch.neo.legacyvault.core.item.ModItems;
import mod.gottsch.neo.legacyvault.core.loot.VaultUpgradeLootModifier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
public class Registration {

	/*
	 * deferred registries
	 */
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, LegacyVault.MOD_ID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, LegacyVault.MOD_ID);

	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
			DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, LegacyVault.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<VaultUpgradeLootModifier>> VAULT_UPGRADE_LOOT_MODIFIER =
			LOOT_MODIFIERS.register("vault_upgrade_drop", () -> VaultUpgradeLootModifier.CODEC);

	/**
	 * 
	 */
	public static void init(IEventBus eventBus) {
		ModBlocks.register(eventBus);
		ModItems.register(eventBus);
		ModBlockEntities.register(eventBus);
		ModContainers.register(eventBus);
		ModAttachments.register(eventBus);
		ENTITIES.register(eventBus);
		PARTICLES.register(eventBus);
		LOOT_MODIFIERS.register(eventBus);
	}

}

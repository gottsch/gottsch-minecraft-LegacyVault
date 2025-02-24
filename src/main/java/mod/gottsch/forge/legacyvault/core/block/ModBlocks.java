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
package mod.gottsch.forge.legacyvault.core.block;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LegacyVault.MOD_ID);

	public static final RegistryObject<RusticVaultBlock> RUSTIC_VAULT = BLOCKS.register("rustic_vault", () -> new RusticVaultBlock(Block.Properties.of().mapColor(MapColor.WOOD).strength(2.5F)));
	public static final RegistryObject<ClassicVaultBlock>CLASSIC_VAULT = BLOCKS.register("classic_vault", () -> new ClassicVaultBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F)));
	public static final RegistryObject<CommunityVaultBlock>COMMUNITY_VAULT = BLOCKS.register("community_vault", () -> new CommunityVaultBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F)));

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}

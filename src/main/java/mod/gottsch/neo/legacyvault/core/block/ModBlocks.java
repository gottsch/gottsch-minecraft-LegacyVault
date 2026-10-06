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
package mod.gottsch.neo.legacyvault.core.block;

import mod.gottsch.neo.legacyvault.core.LegacyVault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class ModBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LegacyVault.MOD_ID);

	public static final DeferredBlock<RusticVaultBlock> RUSTIC_VAULT = BLOCKS.register("rustic_vault", () -> new RusticVaultBlock(Block.Properties.of().mapColor(MapColor.WOOD).strength(2.5F)));
	public static final DeferredBlock<ClassicVaultBlock> CLASSIC_VAULT = BLOCKS.register("classic_vault", () -> new ClassicVaultBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F)));
	public static final DeferredBlock<CommunityVaultBlock> COMMUNITY_VAULT = BLOCKS.register("community_vault", () -> new CommunityVaultBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F)));

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}

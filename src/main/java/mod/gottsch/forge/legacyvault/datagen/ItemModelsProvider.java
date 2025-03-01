/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

/**
 * 
 * @author Mark Gottschling Feb 2, 2025
 *
 */
public class ItemModelsProvider extends ItemModelProvider {

	public ItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, LegacyVault.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// tabs
		singleTexture(
				ModItems.APPLICATION.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/vault_application"));

		singleTexture(
				ModItems.CONTRACT.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/vault_contract"));

		blockItemParent(ModItems.CLASSIC_VAULT);
		blockItemParent(ModItems.RUSTIC_VAULT);
		blockItemParent(ModItems.COMMUNITY_VAULT);
	}

	public ItemModelBuilder blockItemParent(RegistryObject<Item> item) {
		return withExistingParent(item.getId().getPath(), modLoc("block/" + item.getId().getPath()));
	}

	public ItemModelBuilder withExistingParent(RegistryObject<Item> item, ResourceLocation parent) {
		return withExistingParent(item.getId().getPath(), parent);
	}
}

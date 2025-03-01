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
package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * 
 * @author Mark Gottschling on Feb 2, 2025
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
//		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {
			generator.addProvider(new Recipes(generator));
			ModBlockTagsProvider blockTags = new ModBlockTagsProvider(generator, event.getExistingFileHelper());
			generator.addProvider(blockTags);
			generator.addProvider(new ItemTagsProvider(generator, blockTags, event.getExistingFileHelper()));

		}
		if (event.includeClient()) {
//			generator.addProvider(event.includeClient(), new BlockStates(output, event.getExistingFileHelper()));
			generator.addProvider(new ItemModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new LanguageGen(generator, "en_us"));
		}
	}
}
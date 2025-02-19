package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.tags.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mark Gottschling on Feb 2, 2025
 */
public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
	public ItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookup,
							CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookup, blockTagProvider, LegacyVault.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {     
		tag(ModTags.Items.NORMAL_RECIPE).add(ModItems.RUSTIC_VAULT.get());
		tag(ModTags.Items.EASY_RECIPE).add(Items.AIR);
		tag(ModTags.Items.HARD_RECIPE).add(Items.AIR);

		tag(ModTags.Items.VAULT_CONTRACTS)
				.add(ModItems.CONTRACT.get())
				.add(ModItems.APPLICATION.get());
	}
}

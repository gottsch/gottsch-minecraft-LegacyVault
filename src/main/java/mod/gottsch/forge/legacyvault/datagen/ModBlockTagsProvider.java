package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mark Gottschling on Feb 2, 2025
 */
public class ModBlockTagsProvider extends BlockTagsProvider {
    
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
								ExistingFileHelper existingFileHelper) {
    	super(output, lookupProvider, LegacyVault.MOD_ID, existingFileHelper);
	}

	@Override
    protected void addTags(Provider provider) {

	}

}

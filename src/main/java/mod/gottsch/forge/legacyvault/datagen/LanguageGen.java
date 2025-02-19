
package mod.gottsch.forge.legacyvault.datagen;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.ModBlocks;
import mod.gottsch.forge.legacyvault.core.item.ModItems;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * 
 * @author Mark Gottschling on Feb 2, 2025
 *
 */
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput gen, String locale) {
        super(gen, LegacyVault.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {

        // item group

        // items
        add(ModItems.CONTRACT.get(), "Vault Contract");
        add(ModItems.APPLICATION.get(), "Vault Application");

        // blocks
        add(ModBlocks.RUSTIC_VAULT.get(), "Rustic Vault");
        add(ModBlocks.CLASSIC_VAULT.get(), "Classic Vault");

        /*
         *  Util.tooltips
         */
        // general
        add(LangUtil.tooltip("hold_shift"), "Hold [SHIFT] to expand");

        /*
         * Screen
         */
        add(LangUtil.screen("vault.name"),"Legacy Vault");
        add(LangUtil.screen("unlimited_vaults"), "Vaults Remaining: Unlimited");
        add(LangUtil.screen("vaults_remaining"), "Vaults Remaining: %s of %s");
        add(LangUtil.screen("public_vault"), "Public Vault");
    }
}

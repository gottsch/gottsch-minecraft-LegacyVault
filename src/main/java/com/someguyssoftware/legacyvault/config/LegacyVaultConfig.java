package com.someguyssoftware.legacyvault.config;

import com.someguyssoftware.gottschcore.config.AbstractConfig;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.legacyvault.LegacyVault;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Reloading;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * 
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
@EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LegacyVaultConfig extends AbstractConfig {
	protected static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	protected static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;
	
	private static IMod mod;

	public static final General GENERAL;
	
	public static final String CATEGORY_DIV = "##############################";
	public static final String UNDERLINE_DIV = "------------------------------";
	
	static {
		MOD = new Mod(COMMON_BUILDER);
		LOGGING = new Logging(COMMON_BUILDER);
		GENERAL = new General(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
	
	/**
	 * 
	 * @param mod
	 */
	public LegacyVaultConfig(IMod mod) {
		LegacyVaultConfig.mod = mod;
	}
	
	public static class BlockID {
		public static final String VAULT_ID = "vault";
	}
	
	public static class TileEntityID {
		public static final String VAULT_TE_ID = "vault_te";
	}
	
	public static class General {
		public ForgeConfigSpec.BooleanValue  enablePublicVault;
		
		General(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, " General properties for Legacy Vault  mod.", CATEGORY_DIV).push("general");
			
			enablePublicVault = builder
					.comment(" Enables a singular global public vault(s) that can be used by all players from the same location.",
							"ie. a vault block is not 'owned' to 'keyed' to a specific player only.",
							"Typically an admin/server owner would use this to create a central location (or set of locations) where everyone can access their vault.")
					.define("Enable public vault:", false);
		}
	}
	
	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		LegacyVaultConfig.loadConfig(LegacyVaultConfig.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(mod.getId() + "-common.toml"));
	}

	@SubscribeEvent
	public static void onReload(final Reloading configEvent) {
	}

	@Override
	public boolean isEnableVersionChecker() {
		return LegacyVaultConfig.MOD.enableVersionChecker.get();
	}

	@Override
	public void setEnableVersionChecker(boolean enableVersionChecker) {
		LegacyVaultConfig.MOD.enableVersionChecker.set(enableVersionChecker);
	}

	@Override
	public boolean isLatestVersionReminder() {
		return LegacyVaultConfig.MOD.latestVersionReminder.get();
	}

	@Override
	public void setLatestVersionReminder(boolean latestVersionReminder) {
		LegacyVaultConfig.MOD.latestVersionReminder.set(latestVersionReminder);
	}

	@Override
	public boolean isModEnabled() {
		return LegacyVaultConfig.MOD.enabled.get();
	}

	@Override
	public void setModEnabled(boolean modEnabled) {
		LegacyVaultConfig.MOD.enabled.set(modEnabled);
	}

	@Override
	public String getModsFolder() {
		return LegacyVaultConfig.MOD.folder.get();
	}

	@Override
	public void setModsFolder(String modsFolder) {
		LegacyVaultConfig.MOD.folder.set(modsFolder);
	}

	@Override
	public String getConfigFolder() {
		return LegacyVaultConfig.MOD.configFolder.get();
	}

	@Override
	public void setConfigFolder(String configFolder) {
		LegacyVaultConfig.MOD.configFolder.set(configFolder);
	}

	public static IMod getMod() {
		return mod;
	}
}

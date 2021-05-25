package com.someguyssoftware.legacyvault.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.someguyssoftware.gottschcore.config.AbstractConfig;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.legacyvault.LegacyVault;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
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
public class Config extends AbstractConfig {
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
		
		// perform any initializations on data
		Config.init();
	}
	
	/**
	 * 
	 * @param mod
	 */
	public Config(IMod mod) {
		Config.mod = mod;
	}
	
	/**
	 * 
	 * @author Mark Gottschling on May 5, 2021
	 *
	 */
	public static class BlockID {
		public static final String VAULT_ID = "vault";
	}
	
	/**
	 * 
	 * @author Mark Gottschling on May 5, 2021
	 *
	 */
	public static final class TileEntityID {
		public static final String VAULT_TE_ID = "vault_te";
		public static final String MEDIUM_VAULT_TE_ID = "medium_vault_te";
		public static final String LARGE_VAULT_TE_ID = "large_vault_te";
	}
	
	/**
	 * 
	 * @author Mark Gottschling on May 25, 2021
	 *
	 */
	public static final class ContainerID {
		public static final String VAULT_CONTAINER = "vault_container";
		public static final String MEDIUM_VAULT_CONTAINER ="medium_vault_container";
		public static final String LARGE_VAULT_CONTAINER = "large_vault_container";
	}
	
	/**
	 * 
	 * @author Mark Gottschling on May 5, 2021
	 *
	 */
	public static class General {
		public ForgeConfigSpec.BooleanValue  enablePublicVault;
		
		public ConfigValue<List<? extends String>> inventoryWhiteList;
		public ConfigValue<List<? extends String>> inventoryBlackList;
		
		public ConfigValue<List<? extends String>> tagsWhiteList;
		public ConfigValue<List<? extends String>> tagsBlackList;
		
		public List<Pattern> inventoryWhiteListPatterns = new ArrayList<>();
		public List<Pattern> inventoryBlackListPatterns = new ArrayList<>();

		public ForgeConfigSpec.IntValue inventorySize;
		
		General(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, " General properties for Legacy Vault  mod.", CATEGORY_DIV).push("general");
			
			enablePublicVault = builder
					.comment(" Enables a singular global public vault(s) that can be used by all players from the same location.",
							"ie. a vault block is not 'owned' to 'keyed' to a specific player only.",
							"Typically an admin/server owner would use this to create a central location (or set of locations) where everyone can access their vault.")
					.define("Enable public vault:", false);
			
			inventorySize = builder
					.comment(" Maximum capacity of the vault inventory.", 
							" Sizes are 27 (small/standard), 54 (medium), 80 (large).")
					.defineInRange("Vault inventory size:", 54, 27, 80);

			inventoryWhiteList = builder
					.comment(" Allowed Items/Blocks for vault inventory. Must match the Item/Block Registry Name(s). Wildcards ARE supported.  ex. minecraft:plains, minecraft:*stairs")
					.defineList("White list by  Item/Block name:", Arrays.asList(""), s -> s instanceof String);
			
			inventoryBlackList = builder
					.comment(" Disallowed Items/Blocks for vault inventory. Must match the Item/Block Registry Name(s). Wildcards ARE supported.  ex. minecraft:plains, minecraft:*stairs")
					.defineList("Black list by Item/Block name:", Arrays.asList("treasure2:*chest*", "treasure2:cardboard_box","treasure2:milk_crate"), s -> s instanceof String);
			
			tagsWhiteList = builder
					.comment(" Allowed Tags for vault inventory. Must match the Tag Registry Name(s). Wildcards are NOT supported.")
					.defineList("White list by  Tag name:", Arrays.asList(""), s -> s instanceof String);
			
			tagsBlackList = builder
					.comment(" Disallowed Tags for vault inventory. Must match the Tag Registry Name(s). Wildcards are NOT supported.")
					.defineList("Black list by  Tag name:", Arrays.asList(""), s -> s instanceof String);
		}
		
		/**
		 * 
		 */
		public void init() {
			for(String name : inventoryWhiteList.get()) {
				inventoryWhiteListPatterns.add(Pattern.compile(name));
			}
			for(String name : inventoryBlackList.get()) {
				inventoryBlackListPatterns.add(Pattern.compile(name));
			}
		}
	}
	
	public static void init() {
		Config.GENERAL.init();
	}
	
	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		Config.loadConfig(Config.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(mod.getId() + "-common.toml"));
	}

	@SubscribeEvent
	public static void onReload(final Reloading configEvent) {
	}

	@Override
	public boolean isEnableVersionChecker() {
		return Config.MOD.enableVersionChecker.get();
	}

	@Override
	public void setEnableVersionChecker(boolean enableVersionChecker) {
		Config.MOD.enableVersionChecker.set(enableVersionChecker);
	}

	@Override
	public boolean isLatestVersionReminder() {
		return Config.MOD.latestVersionReminder.get();
	}

	@Override
	public void setLatestVersionReminder(boolean latestVersionReminder) {
		Config.MOD.latestVersionReminder.set(latestVersionReminder);
	}

	@Override
	public boolean isModEnabled() {
		return Config.MOD.enabled.get();
	}

	@Override
	public void setModEnabled(boolean modEnabled) {
		Config.MOD.enabled.set(modEnabled);
	}

	@Override
	public String getModsFolder() {
		return Config.MOD.folder.get();
	}

	@Override
	public void setModsFolder(String modsFolder) {
		Config.MOD.folder.set(modsFolder);
	}

	@Override
	public String getConfigFolder() {
		return Config.MOD.configFolder.get();
	}

	@Override
	public void setConfigFolder(String configFolder) {
		Config.MOD.configFolder.set(configFolder);
	}

	public static IMod getMod() {
		return mod;
	}
}

/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import mod.gottsch.forge.gottschcore.config.AbstractConfig;
import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

/**
 * 
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
@EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config extends AbstractConfig {

	public static final String GENERAL_CATEGORY = "general";
	public static final String PUBLIC_VAULT_CATEGORY = "public-vault";
	public static final String DATABASE_CATEGORY = "database";
	public static final String CATEGORY_DIV = "##############################";
	public static final String UNDERLINE_DIV = "------------------------------";

	public static Config instance = new Config();
	
	public static ForgeConfigSpec SERVER_SPEC;
	public static ForgeConfigSpec COMMON_SPEC;
	
	public static class ServerConfig {
		public static General GENERAL;
		public static PublicVault PUBLIC_VAULT;
		public static Db DATABASE;
	}
	
	public static class CommonConfig {
		public static Logging LOGGING;
	}
	
	/**
	 * 
	 */
	private Config() {}
	
	/**
	 * 
	 */
	public static void register() {
		registerServerConfigs();
		registerCommonConfigs();
	}

	/**
	 * 
	 */
	private static void registerServerConfigs() {
		ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
		ServerConfig.GENERAL = new General(SERVER_BUILDER);
		ServerConfig.PUBLIC_VAULT = new PublicVault(SERVER_BUILDER);
		ServerConfig.DATABASE = new Db(SERVER_BUILDER);
		SERVER_SPEC = SERVER_BUILDER.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
	}

	private static void registerCommonConfigs() {
		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
		CommonConfig.LOGGING = new Logging(COMMON_BUILDER);
		COMMON_SPEC = COMMON_BUILDER.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
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
	public static final class BlockEntityID {
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
	 * @author Mark Gottschling on May 25, 2021
	 *
	 */
	public static final class CapabilityID {
		public static final String PLAYER_PROVIDER = "player_cap_provider";
	}

	/**
	 * 
	 * @author Mark Gottschling on Jun 9, 2021
	 *
	 */
	public static class Db {
		public ConfigValue<String> user;
		public ConfigValue<String> password;

		Db(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, " Database properties for Legacy Vault  mod.", CATEGORY_DIV).push(DATABASE_CATEGORY);
			user = builder
					.comment("User for H2 DB access.")
					.define("User", "sa");

			password = builder
					.comment("Password for H2 DB access.")
					.define("Password", "sa");

			builder.pop();
		}
	}

	/**
	 * 
	 * @author Mark Gottschling on May 5, 2021
	 *
	 */
	public static class General {
		public static final int MAX_INVENTORY_SIZE = 91;
		
		public ConfigValue<List<? extends String>> inventoryWhiteList;
		public ConfigValue<List<? extends String>> inventoryBlackList;

		public ConfigValue<List<? extends String>> tagsWhiteList;
		public ConfigValue<List<? extends String>> tagsBlackList;

		public List<Pattern> inventoryWhiteListPatterns = new ArrayList<>();
		public List<Pattern> inventoryBlackListPatterns = new ArrayList<>();

		public ForgeConfigSpec.IntValue inventorySize;
		public ForgeConfigSpec.IntValue stackSize;
		public BooleanValue enableLimitedVaults;
		public IntValue vaultsPerPlayer;
//		public ConfigValue<String> recipeDifficulty;
		
		private static final Predicate<Object> STRING_PREDICATE = s -> s instanceof String;

		public General(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, 
					" General properties for Legacy Vault  mod.", 
					" Note: As of mc1.19.2, the recipe conditions are data-driven via Tags.",
					" Therefor, ensure to update tags at data.legacyvault.tags.items.difficult accordingly.",
					" ie. Add legacyvault/vault to the values list in one of the difficulty tags. Default = Normal.",
					CATEGORY_DIV).push(GENERAL_CATEGORY);

			inventorySize = builder
					.comment(" Maximum capacity of the vault inventory.", 
							" Sizes are 27 (small/standard), 54 (medium), 91 (large). (**91 is odd, I know, but it fits within the 256x256, without having to scroll.)")
					.defineInRange("Vault inventory size:", 27, 27, 91);

			stackSize = builder
					.comment(" Maximum item stack size in a vault.")
					.defineInRange("Maximum item stack size:", 64, 1, 1024);

			enableLimitedVaults = builder
					.comment(" Enables a limited number of vaults per player per world.",
							" Default value = true, with 3 vaults per player.")
					.define("Enable limited vaults player:", true);

			vaultsPerPlayer = builder
					.comment(" The number of vaults each player can place per world.", " Enable public vault' must be disabled.")
					.defineInRange("Number of vaults per player:", 3, 1, 100);

//			recipeDifficulty = builder
//					.comment("Values are [easy | normal | hard]")
//					.define("Recipe Difficulty", "normal");

			inventoryWhiteList = builder
					.comment(" Allowed Items/Blocks for vault inventory. Must match the Item/Block Registry Name(s). Regex IS supported.  ex. minecraft:dirt, (minecraft:)+([a-z0-9_]+)stairs")
					.defineList("White list by  Item/Block name:", new ArrayList<String>(), STRING_PREDICATE);

			inventoryBlackList = builder
					.comment(" Disallowed Items/Blocks for vault inventory. Must match the Item/Block Registry Name(s). Regex IS supported.  ex. minecraft:dirt, (minecraft:)+([a-z0-9_]+)stairs")
					.defineList("Black list by Item/Block name:", Arrays.asList("(treasure2:)+([a-z0-9_]+)(chest)+([a-z0-9_]?)", "(treasure2:)+([a-z0-9_]+)(strongbox)+", "treasure2:cardboard_box","treasure2:milk_crate"), STRING_PREDICATE);

			tagsWhiteList = builder
					.comment(" Allowed Tags for vault inventory. Must match the Tag Registry Name(s). Regex is NOT supported.")
					.defineList("White list by  Tag name:", new ArrayList<String>(), STRING_PREDICATE);

			tagsBlackList = builder
					.comment(" Disallowed Tags for vault inventory. Must match the Tag Registry Name(s). Regex is NOT supported.")
					.defineList("Black list by  Tag name:", new ArrayList<String>(), STRING_PREDICATE);

			builder.pop();
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

	/**
	 * 
	 * @author Mark Gottschling on Jun 6, 2021
	 *
	 */
	public static class PublicVault {
		public BooleanValue  enablePublicVault;
		public ConfigValue<List<? extends String>> playerWhiteList;
		public ConfigValue<List<? extends String>> playerBlackList;

		PublicVault(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, 
					" Public Vault properties for Legacy Vault  mod.", 
					" Note: As of mc1.19.2, the recipe conditions are data-driven via Tags.",
					" Therefor, ensure to update tags at data.legacyvault.tags.items.difficult accordingly.",
					" ie. You probably don't want vault recipes enabled if Public Vault is enabled.",
					CATEGORY_DIV).push(PUBLIC_VAULT_CATEGORY);

			enablePublicVault = builder
					.comment(" Enables a singular global public vault(s) that can be used by all players from the same location.",
							" ie. a vault block is not 'owned' or 'keyed' to a specific player only.",
							" Typically an admin/server owner would use this to create a central location (or set of locations) where everyone can access their vault.")
					.define("Enable public vault:", false);

			playerWhiteList = builder
					.comment(" Allowed players for vault inventory. Must match the Player UUID(s). Wildcards are NOT supported.",
							"If both White and Black lists are empty, then all players have access.")
					.defineList("White list by player uuid:", new ArrayList<String>(), s -> s instanceof String);

			playerBlackList = builder
					.comment(" Disallowed players for vault inventory. Must match the Player UUID(s). Wildcards are NOT supported.",
							"If both White and Black lists are empty, then all players have access.")
					.defineList("Black list by player uuid:", new ArrayList<String>(), s -> s instanceof String);

			builder.pop();
		}
	}

	public static void init() {
		Config.ServerConfig.GENERAL.init();
	}
	
	@Override
	public String getLogsFolder() {
		return Config.CommonConfig.LOGGING.folder.get();
	}
	
	public void setLogsFolder(String folder) {
		Config.CommonConfig.LOGGING.folder.set(folder);
	}
	
	@Override
	public String getLoggingLevel() {
		return Config.CommonConfig.LOGGING.level.get();
	}
}

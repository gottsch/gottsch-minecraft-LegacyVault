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
package mod.gottsch.forge.legacyvault.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import mod.gottsch.forge.gottschcore.config.AbstractConfig;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.inventory.VaultSlotSize;
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
@EventBusSubscriber(modid = LegacyVault.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config extends AbstractConfig {

	public static final String GENERAL_CATEGORY = "general";
	public static final String PERSONAL_VAULT_CATEGORY = "personalVault";
	public static final String COMMUNITY_VAULT_CATEGORY = "communityVault";
	public static final String CATEGORY_DIV = "##############################";
	public static final String UNDERLINE_DIV = "------------------------------";

	public static Config instance = new Config();

	public static ForgeConfigSpec SERVER_SPEC;
	public static ForgeConfigSpec COMMON_SPEC;

	public static class ServerConfig {
		public static General GENERAL;
		public static PersonalConfig PERSONAL;
		public static CommunityConfig COMMUNITY;
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
		ServerConfig.PERSONAL = new PersonalConfig(SERVER_BUILDER);
		ServerConfig.COMMUNITY = new CommunityConfig(SERVER_BUILDER);
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
	public static class General {
		public static final int MAX_INVENTORY_SIZE = 84;

		public ConfigValue<List<? extends String>> inventoryWhitelist;
		public ConfigValue<List<? extends String>> inventoryBlacklist;

		public List<Pattern> inventoryWhitelistPatterns = new ArrayList<>();
		public List<Pattern> inventoryBlacklistPatterns = new ArrayList<>();

		public ConfigValue<String> inventorySize;
		public ForgeConfigSpec.IntValue maxSlotStackSize;

		public ConfigValue<String> recipeDifficulty;

		// an internal config property that is calculated after the config is loaded.
		public int resolvedSize;

		private static final Predicate<Object> STRING_PREDICATE = s -> s instanceof String;

		public General(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV,
					" GENERAL PROPERTIES",
					CATEGORY_DIV).push(GENERAL_CATEGORY);

			inventorySize = builder
					.comment(" Maximum capacity of the vault inventory.",
							" Sizes are standard/vanilla (27), large/double (54), xlarge (91).")
					.define("inventorySize", "standard");

			maxSlotStackSize = builder
					.comment(" Maximum item stack size in a vault slot.",
							" NOTE this maximum will not overwrite the item's max stack size.",
							" Ex. if the item max stack size = 64, the vault slot will max out at 64 even if it is set at 100.")
					.defineInRange("maxStackSize", 64, 1, 1024);

			recipeDifficulty = builder
					.comment("Values are [easy | normal | hard]")
					.defineInList("recipeDifficulty", "normal", Arrays.asList("easy", "normal", "hard"));

			inventoryWhitelist = builder
					.comment(" Allowed Items/Blocks for vault inventory.",
							" Must match the Item/Block Registry Name(s). Regex IS supported.  ex. minecraft:dirt, (minecraft:)+([a-z0-9_]+)stairs",
							" Tags (legacyvault:items/vault_whitelist) takes precedence.")
					.defineList("inventoryWhitelist", new ArrayList<String>(), STRING_PREDICATE);

			inventoryBlacklist = builder
					.comment(" Disallowed Items/Blocks for vault inventory.",
							" Must match the Item/Block Registry Name(s). Regex IS supported.  ex. minecraft:dirt, (minecraft:)+([a-z0-9_]+)stairs",
							" Tags (legacyvault:items/vault_blacklist) takes precedence.")
					.defineList("inventoryBlacklist", Arrays.asList("(treasure2:)+([a-z0-9_]+)(chest)+([a-z0-9_]?)", "(treasure2:)+([a-z0-9_]+)(strongbox)+", "treasure2:cardboard_box","treasure2:milk_crate"), STRING_PREDICATE);

			builder.pop();
		}

		/**
		 *
		 */
		public void init() {
			for(String name : inventoryWhitelist.get()) {
				inventoryWhitelistPatterns.add(Pattern.compile(name));
			}
			for(String name : inventoryBlacklist.get()) {
				inventoryBlacklistPatterns.add(Pattern.compile(name));
			}

			// map sizes
			try {
				resolvedSize = VaultSlotSize.valueOf(inventorySize.get().toUpperCase()).getSize();
			} catch (Exception e) {
				LegacyVault.LOGGER.warn("unrecognized 'inventorySize' value of {}. using standard size of {}", inventorySize.get(), VaultSlotSize.STANDARD.getSize());
				resolvedSize = VaultSlotSize.STANDARD.getSize();
			}
		}
	}

	public static class PersonalConfig {
		public BooleanValue enabled;
		public BooleanValue unlimitedVaults;
		public IntValue vaultsPerPlayer;

		PersonalConfig(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV,
					" PERSONAL VAULT PROPERTIES",
					" Note: As of mc1.19.2, the recipe conditions are data-driven via Tags.",
					" Therefor, ensure to update tags at data.legacyvault.tags.items.difficulty accordingly.",
					" A 'legacyvault:classic_vault' must be in one of the difficulty tags to enable the recipe.",
					" Remove the item from all the tags to disable the recipe altogether.",
					" Default = Normal.",
					CATEGORY_DIV).push(PERSONAL_VAULT_CATEGORY);

			enabled = builder
					.comment(" Enables vaults exclusive to each player.",
							" The player can access their inventory only from their owned vaults.")
					.define("enabled", true);

			unlimitedVaults = builder
					.comment(" Enables unlimited number of vaults per player per world.",
							" Default value = false, with 3 vaults per player.")
					.define("unlimitedVaults", false);

			vaultsPerPlayer = builder
					.comment(" The number of vaults each player can place per world.")
					.defineInRange("vaultsPerPlayer", 3, 1, 100);

			builder.pop();
		}
	}

	public static class CommunityConfig {
		public BooleanValue enabled;
		public ConfigValue<List<? extends String>> playerWhiteList;
		public ConfigValue<List<? extends String>> playerBlackList;

		CommunityConfig(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV,
					" COMMUNITY VAULT PROPERTIES",
					CATEGORY_DIV).push(COMMUNITY_VAULT_CATEGORY);

			enabled = builder
					.comment(" Enables a community vault(s) that can be used by all players from the same location to access their personal inventory (much like an Ender Chest).",
							" ie. a vault block is not 'owned' by specific player only.",
							" Typically an admin/server owner would use this to create a central location (or set of locations) where everyone can access their vault inventory.")
					.define("enabled", true);

			// TODO add a config list of players who are allowed to place community vaults (besides OPS/Admin)

			playerWhiteList = builder
					.comment(" Allowed players for community vault inventory. Must match the Player UUID(s). Wildcards are NOT supported.",
							"If both White and Black lists are empty, then all players have access.")
					.defineList("playerWhitelist", new ArrayList<String>(), s -> s instanceof String);

			playerBlackList = builder
					.comment(" Disallowed players for community vault inventory. Must match the Player UUID(s). Wildcards are NOT supported.",
							"If both White and Black lists are empty, then all players have access.")
					.defineList("playerBlacklist", new ArrayList<String>(), s -> s instanceof String);

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

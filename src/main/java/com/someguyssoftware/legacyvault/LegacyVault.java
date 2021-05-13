package com.someguyssoftware.legacyvault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.init.LegacyVaultSetup;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
@Mod(value = LegacyVault.MODID)
@ModInfo(
		modid = LegacyVault.MODID, 
		name = LegacyVault.NAME, 
		version = LegacyVault.VERSION, 
		minecraftVersion = "1.16.5", 
		forgeVersion = "36.1.0", 
		updateJsonUrl = LegacyVault.UPDATE_JSON_URL)
@Credits(values = { "LegacyVault was first developed by Mark Gottschling on Apr 28, 2021."})
public class LegacyVault implements IMod {
	// logger
	public static Logger LOGGER = LogManager.getLogger(LegacyVault.NAME);

	// constants
	public static final String MODID = "legacyvault";
	protected static final String NAME = "Legacy Vault";
	protected static final String VERSION = "0.0.1";
	protected static final String UPDATE_JSON_URL = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-InterWorld-Bank/1.16.5-master/update.json";

	public static LegacyVault instance;
	private static Config config;
	public static IEventBus MOD_EVENT_BUS;

	private boolean  hardCore = false;
	
	/**
	 * 
	 */
	public LegacyVault() {
		LegacyVault.instance = this;
		LegacyVault.config = new Config(this);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

		// Register the setup method for modloading
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(LegacyVaultSetup::common);

		Config.loadConfig(Config.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve("legacyvault-common.toml"));

		// test accessing the logging properties
		Config.LOGGING.filename.get();

		// needs to be registered here instead of @Mod.EventBusSubscriber because we need to pass in a constructor argument
//		MinecraftForge.EVENT_BUS.register(new WorldEventHandler(getInstance()));
//		MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
		MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

	}
	
	public static void clientOnly() {
//		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
//		MOD_EVENT_BUS.register(ClientEventHandler.class);
	}
	
	/**
	 * ie. preint
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	private void setup(final FMLCommonSetupEvent event) {

	}


	@Override
	public IMod getInstance() {
		return LegacyVault.instance;
	}

	@Override
	public String getId() {
		return LegacyVault.MODID;
	}

	@Override
	public IConfig getConfig() {
		return LegacyVault.config;
	}

	public void setHardCore(boolean hardCore) {
		this.hardCore  = hardCore;
	}
	
	public boolean isHardCore() {
		return hardCore;
	}
}


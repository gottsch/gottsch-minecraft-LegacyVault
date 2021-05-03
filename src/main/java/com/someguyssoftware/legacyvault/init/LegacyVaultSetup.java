/**
 * 
 */
package com.someguyssoftware.legacyvault.init;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.LegacyVaultConfig;
import com.someguyssoftware.legacyvault.db.DbManager;
import com.someguyssoftware.legacyvault.exception.DbInitializationException;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
public class LegacyVaultSetup {
	/**
	 * 
	 * @param event
	 */
	public static void common(final FMLCommonSetupEvent event) {
		// add mod specific logging
		IModSetup.addRollingFileAppender(LegacyVault.instance.getName(), null);
		
		// start the database
		try {
			DbManager.start((LegacyVaultConfig) LegacyVault.instance.getConfig());
		} catch (DbInitializationException e) {
			LegacyVault.LOGGER.error("Unable to start database manager:", e);
//			getConfig().setModEnabled(false);
			// TODO create another PlayerLoggedIn Event that checks if the database failed initialization and inform player.
		}
	}
}
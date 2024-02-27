/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.db;


import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mod.gottsch.shade.org.h2.v2_1_210.tools.RunScript;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.db.entity.Account;
import mod.gottsch.forge.legacyvault.exception.DbInitializationException;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
public class DbManager {

	// LOGGER
	public static Logger LOGGER = LogManager.getLogger(LegacyVault.MODID);

	public static final String LEGACY_DB_FILE_NAME = "vault";

	public static final String DB_FILE_NAME = "vault2x";
	public static final String DB_EXTENSION = ".mv.db";
	public static final String DUMP_FILE_NAME = "dump.sql";
	public static final String BACKUP_EXTENSION = ".bak";

	private static final String DRIVER_CLASSNAME = "mod.gottsch.shade.org.h2.v2_1_210.Driver";

	private static DbManager instance;
	// JPA
	private JdbcConnectionPool connectionPool;
	private Connection connection;

	private final Path legacyDbPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MODID, LEGACY_DB_FILE_NAME).toAbsolutePath();
	private final Path legacyDbFilePath = Paths.get(legacyDbPath.toString() + DB_EXTENSION);
	private final Path dbPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MODID, DB_FILE_NAME).toAbsolutePath();
	private final Path dbFilePath = Paths.get(dbPath.toString() + DB_EXTENSION);

	private final Path dumpFilePath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MODID, DUMP_FILE_NAME).toAbsolutePath();

	/*
	 * In order to use ORM Lite, you must use a proprietary Connection object,
	 * hence there will be two connections to use the H2 database.
	 */
	// ORM Lite
	private JdbcConnectionSource connSource;
	private Dao<Account, String> accountDao;

	/**
	 * Private constructor
	 */
	private DbManager() {
		// create a singleton
	}

	/**
	 * @return
	 */
	public static synchronized DbManager getInstance() {
		return instance;
	}

	public static void start() throws DbInitializationException {
		if (instance != null) {
			DbManager.shutdown();
		}

		if (instance == null) {
			LOGGER.debug("Creating new instance of DbManager");
			try {
				instance = new DbManager();
				instance.startDbManager();
			} catch (ClassNotFoundException | SQLException e) {
				LegacyVault.LOGGER.error("An error occurred during initialization ->", e);
				throw new DbInitializationException("Unable to create an instance of DbManager.");
			}
		}
	}

	private synchronized void startDbManager() throws ClassNotFoundException, SQLException {
		LOGGER.info("Initializing DbManager...");
		Connection conn = null;
		JdbcConnectionSource connSource = null;

		LOGGER.debug("path to db file -> {}", legacyDbFilePath.toString());

		// first check for legacy db
		if (isOldVersionDb() && !isNewVersionDb()) {
			LegacyVault.LOGGER.debug("performing db conversion...");
			boolean convertResult = convertLegacyDb();
		}

		// load the driver class
		Class.forName(DRIVER_CLASSNAME);
		LOGGER.info("loaded h2 driver...");

		// create the connection
		String databaseUrl = String.format("jdbc:h2:%s;USER=%s;PASSWORD=%s;", dbPath.toString(), ServerConfig.DATABASE.user.get(), ServerConfig.DATABASE.password.get());
		LOGGER.debug("db url -> {}", databaseUrl);
		try {
			conn = createConnection();
			if (conn == null) {
				LOGGER.warn("Unable to connect JPA to h2 database.");
				return;
			}
		} catch (Exception e) {
			/*
			 * there is an error in the connection, therefor cannot run the backup script.
			 */
			if (isDumpFile()) {
				try {
					renameDb(this.dbFilePath);
				} catch (Exception e2) {
					LegacyVault.LOGGER.warn("Unable to backup database, while recovering from previous exception.\nDatabase connection not established - continuing without LegacyVault functionality.", e);
					return;
				}
				// re-try connecting, which will create a new db
				try {
					conn = createConnection();
				} catch (Exception e2) {
					LegacyVault.LOGGER.warn("Unable to connect to new db, while recovering from previous exception.\nDatabase connection not established - continuing without LegacyVault functionality.", e);
					revertRename();
					return;
				}

				try {
					importDb(this.dbPath, Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MODID, DUMP_FILE_NAME)); // "./config/legacyvault/dump.sql"
				} catch (Exception e2) {
					LegacyVault.LOGGER.error("Unable to import database data, while recovering from previous exception.\nDatabase connection not established - continuing without LegacyVault functionality.", e);
					revertRename();
					return;
				}
			}
		}

		// set the connection
		setConnection(conn);

		// look for file --> vault.mv.db
		LOGGER.debug("path to db file -> {}", dbFilePath.toString());
		boolean pathExists = Files.exists(dbFilePath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
		LOGGER.debug("path exists -> {}", pathExists);

		// execute the script
		LOGGER.info("executing sql scripts...");
		try {
			// open a stream to the sql file
			String sqlScriptFilePath = "/" + LegacyVault.MODID + ".sql";
			LOGGER.debug("script path -> {}", sqlScriptFilePath);
			InputStream is = getClass().getResourceAsStream(sqlScriptFilePath);

			if (is == null) {
				LOGGER.error("Unable to locate legacyvault.sql resource");
				// TODO create custom exception
				// TODO method should throws exception
				// TODO mod handles the thrown exception. ex. disable the mod if thrown.
				// TODO display message to the user in the chat
			}
			Reader reader = new InputStreamReader(is);
			// run the script (creates and populates tables)
			RunScript.execute(conn, reader);

			runBackupScript();
		} catch (Exception e) {
			LOGGER.error("Error running sql script:", e);
		}

		// close the JPA connection
		getConnection().close();

		/*
		 * open the ORM Lite connection and setup the daos
		 */
		connSource = new JdbcPooledConnectionSource(databaseUrl);
		setConnSource(connSource);

		// setup the daos;
		accountDao = DaoManager.createDao(connSource, Account.class);
		//		containerGroupDao = DaoManager.createDao(connSource, LootContainerHasGroup.class);
		//		groupItemDao = DaoManager.createDao(connSource, LootGroupHasItem.class);


		LOGGER.info("...complete.");
	}

	private void revertRename() {

		// revert rename
		try {
			Files.copy(Paths.get(dbFilePath + ".bak"), dbFilePath, StandardCopyOption.REPLACE_EXISTING);
		} catch(Exception ignored){}
	}

	private Connection createConnection() throws Exception {
		JdbcConnectionPool connectionPool = JdbcConnectionPool.create(String.format("jdbc:h2:%s", dbPath.toString()), ServerConfig.DATABASE.user.get(), ServerConfig.DATABASE.password.get() );
		setConnectionPool(connectionPool);
		return connectionPool.getConnection();
	}

	protected boolean isOldVersionDb() {
		return Files.exists(this.legacyDbFilePath, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
	}

	protected boolean isNewVersionDb() {
		return Files.exists(this.dbFilePath, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
	}

	protected boolean isDumpFile() {
		return Files.exists(this.dumpFilePath, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
	}

	protected boolean convertLegacyDb() {
		boolean isDumpSuccess = false;
		try {
			dumpLegacyDb();
			isDumpSuccess = true;
		} catch(Exception e) {
			LegacyVault.LOGGER.warn("Unable to recover old database. Adding a backup copy and generating a new database.");
		}
		// rename old db
		try {
			renameLegacyDb();
		} catch(Exception e) {
			LegacyVault.LOGGER.warn("Unable to rename old database. A new database has been created (vault2x.vm.db.) Please remove/move the old database.");
		}

		if (isDumpSuccess) {
			try {
				importIntoNewDb();
				return true;
			} catch(Exception e) {
				LegacyVault.LOGGER.error("Unable to import old database data into new database", e);
			}
		}
		return false;
	}

	protected void dumpLegacyDb() throws Exception {
			/*
			 * the recovery tool is limited in that it will only execute against a db in the current
			 * working directory. ie cannot pass the path as a param, only the filename.
			 */

			/*
			 * this Apache solution will only work if the user has Java install with a PATH variable to it.
			 * the Minecraft App in the Microsoft store is not accessible. If this fails need to attempt
			 * to open the db and dump it using dumpLegacyDb()
			 */
			// TODO use const
			try {
				CommandLine cmdLine = CommandLine.parse("java -cp ../../mods/legacyvault-1.20.1-1.4.0.jar org.h2.tools.Recover");
				DefaultExecutor executor = DefaultExecutor.builder().setWorkingDirectory(new File("./config/legacyvault")).get();
				int exitValue = executor.execute(cmdLine);
			} catch(Exception e) {
				// attempt to create a backup using the script
				if (!runBackupScript()) {
					throw new Exception("unable to run Recover nor backup script on old db:", e);
				}
			}
	}

	@Deprecated
	protected void renameLegacyDb() throws Exception {
		Path renamedPath = Files.move(this.legacyDbFilePath, Paths.get(this.legacyDbFilePath.toString() + ".bak"));
		LegacyVault.LOGGER.debug("result of rename -> {}", renamedPath.toString());
	}

	protected void renameDb(Path path) throws Exception {
		Path renamedPath = Files.move(path, Paths.get(path + ".bak"));
		LegacyVault.LOGGER.debug("result of rename -> {}", renamedPath.toString());
	}

	/**
	 * this is strictly for importing the legacy recovery file...
	 * @throws Exception
	 */
	@Deprecated
	protected void importIntoNewDb() throws Exception {
		// TODO don't hard-code path
		new RunScript().runTool("-url", String.format("jdbc:h2:%s", this.dbPath.toAbsolutePath().toString()), "-script", "./config/legacyvault/vault.h2.sql");
	}

	protected  void importDb(Path destDbPath, Path relativeScriptPath) throws Exception {
		new RunScript().runTool("-url", String.format("jdbc:h2:%s", destDbPath.toString()), "-script", relativeScriptPath.toString());
	}

	/**
	 * 
	 */
	public static void shutdown() {
		getInstance().getConnSource().closeQuietly();
		getInstance().getConnectionPool().dispose();
//		getInstance().getServer().stop();
		instance = null;
		LOGGER.info("Shut down of DbManager complete.");
	}

	// TODO
	protected static void runCreateScript() {

	}

	public static boolean runBackupScript() {
		Connection conn = null;
		// dump the db
		String dumpSqlScriptFilePath = "/" + LegacyVault.MODID + "_dump.sql";
		LOGGER.debug("script path -> {}", dumpSqlScriptFilePath);
		try(InputStream is = getInstance().getClass().getResourceAsStream(dumpSqlScriptFilePath);
			Reader reader = new InputStreamReader(is)) {
			// run the script (creates and populates tables)
			if (getInstance().getConnection() == null || getInstance().getConnection().isClosed()) {
				conn = getInstance().getConnectionPool().getConnection();
			} else {
				conn = getInstance().getConnection();
			}
			RunScript.execute(conn, reader);
			conn.close();
		} catch(SQLException | IOException e) {
			LOGGER.error("Error running dump sql script:", e);
			return false;
		}
		finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (Exception e) {
				LegacyVault.LOGGER.error("Unable to close db connection:", e);
			}
		}
		return true;
	}

	/**
	 * 
	 * @param account
	 */
	public synchronized void saveAccount(Account account) {
		try {
			accountDao.createOrUpdate(account);
		} catch (SQLException e) {
			LegacyVault.LOGGER.error("An error occurred attempting to create/update account:", e);
		}		
	}
	
	/**
	 * 
	 * @param uuid
	 * @param version
	 * @param type
	 * @return
	 */
	public synchronized Optional<Account> getAccount(String uuid, String version, String type) {
		List<Account> accounts = null;
		Optional<Account> account = Optional.empty();
		try {
			Map<String, Object> fieldValues = new HashMap<>();
			fieldValues.put("uuid", uuid);
			fieldValues.put("mc_version", version);
			fieldValues.put("game_type", type);
			accounts = accountDao.queryForFieldValues(fieldValues);
			if (accounts.size() > 0) {
				account = Optional.ofNullable(accounts.get(0));
			}
			else {
				account = Optional.of(new Account().setUuid(uuid).setVersion(version).setGameType(type));
			}
		}
		catch(SQLException e) {
			LegacyVault.LOGGER.error("error occurred attempting to get account from db -> ", e);
			// TODO call Recovery here.
		}
		return account;
	}
	
	//		/**
	//		 * 
	//		 * @param rarity
	//		 * @return
	//		 */
	//		public LootContainer selectContainer(Random random, final Rarity rarity) {
	//			List<Rarity> rarities = new ArrayList<>();
	//			rarities.add(rarity);
	//			return selectContainer(random, rarities);
	//		}
	//		
	//		/**
	//		 * 
	//		 * @param random
	//		 * @param rarity
	//		 * @return
	//		 */
	//		public LootContainer selectContainer(Random random, List<Rarity> rarity) {
	//			LootContainer container = LootContainer.EMPTY_CONTAINER;
	//			// select the loot container by rarity
	//			List<LootContainer> containers = DbManager.getInstance().getContainersByRarity(rarity);
	//			if (containers != null && !containers.isEmpty()) {
	//				/*
	//				 * get a random container
	//				 */
	//				if (containers.size() == 1) {
	//					container = containers.get(0);
	//				}
	//				else {
	//					container = containers.get(RandomHelper.randomInt(random, 0, containers.size()-1));
	//				}
	//				LegacyVault.LOGGER.info("Chosen container:" + container);
	//			}
	//			return container;
	//		}
	//		
	//		/**
	//		 * 
	//		 * @param rarity
	//		 * @return
	//		 */
	//		public List<LootContainer> getContainersByRarity(Rarity rarity) {		
	//			List<LootContainer> list = null;
	//			try {
	//				list = containerDao.queryBuilder().where()
	//				         .eq("rarity", rarity.name())
	//				         .query();			
	//			} catch (SQLException e) {
	//				LegacyVault.LOGGER.error("An error occurred attempting to retrieve containers by rarity:", e);
	//			}		
	//			return list;
	//		}
	//		
	//		/**
	//		 * 
	//		 * @param rarity
	//		 * @return
	//		 */
	//		public List<LootContainer> getContainersByRarity(List<Rarity> rarity) {
	//			// convert rarity list to string
	//			List<String> rarityNameList = rarity.stream().map(r -> r.name()).collect(Collectors.toList());				
	//					
	//			// query for all accounts that have that password
	//			List<LootContainer> list = null;
	//			try {
	//				list = containerDao.queryBuilder().where()
	//						.in("rarity", rarityNameList)
	//				         .query();			
	//			} catch (SQLException e) {
	//				LegacyVault.LOGGER.error("An error occurred attempting to retrieve containers by rarity:", e);
	//			}		
	//			return list;
	//		}
	//		
	//		/**
	//		 * Fetches ALL groups by container regardless of special column.
	//		 * @param id
	//		 * @return
	//		 */
	//		public List<LootContainerHasGroup> getGroupsByContainer(Integer id) {
	//			// inner join to get groups
	//			List<LootContainerHasGroup> containerGroups = null;
	//			try {
	//				containerGroups = containerGroupDao.queryBuilder()
	//						.selectColumns(LootContainerHasGroup.GROUP_ID_FIELD_NAME, "group_weight", "min_items", "max_items", "ordering", "special")
	//						.where()
	//						.eq(LootContainerHasGroup.CONTAINER_ID_FIELD_NAME, id)
	//						.query();
	//				
	//			}
	//			catch(SQLException e) {
	//				e.printStackTrace();
	//			}		
	//			return containerGroups;
	//		}
	//		
	//		/**
	//		 * 
	//		 * @param id
	//		 * @return
	//		 */
	//		public List<LootContainerHasGroup> getGroupsByContainer(Integer id, boolean isSpecial) {
	//			// inner join to get groups
	//			List<LootContainerHasGroup> containerGroups = null;
	//			try {
	//				containerGroups = containerGroupDao.queryBuilder()
	//						.selectColumns(LootContainerHasGroup.GROUP_ID_FIELD_NAME, "group_weight", "min_items", "max_items", "ordering", "special")
	//						.where()
	//						.eq(LootContainerHasGroup.CONTAINER_ID_FIELD_NAME, id)
	//						.and()
	//						.eq(LootContainerHasGroup.SPECIAL_FIELD_NAME, isSpecial)
	//						.query();
	//				
	//			}
	//			catch(SQLException e) {
	//				e.printStackTrace();
	//			}		
	//			return containerGroups;
	//		}
	//		
	//		/**
	//		 * 
	//		 * @param cg
	//		 * @return
	//		 */
	//		public List<LootGroupHasItem> getItemsByContainer(LootContainerHasGroup cg) {
	//			// inner join to get groups
	//			List<LootGroupHasItem> groupItems = null;
	//			try {
	//				QueryBuilder<LootContainerHasGroup, String> qb = containerGroupDao.queryBuilder();
	//				
	//				qb.selectColumns(LootContainerHasGroup.GROUP_ID_FIELD_NAME)
	//				.where()
	//				.eq(LootContainerHasGroup.CONTAINER_ID_FIELD_NAME, cg);
	//
	//				// outer join to groups-items
	//				QueryBuilder<LootGroupHasItem, String> qb2 = groupItemDao.queryBuilder();
	//				groupItems = qb2
	//						.selectColumns(LootGroupHasItem.ITEM_ID_FIELD_NAME)
	//						.where()
	//						.in(LootGroupHasItem.GROUP_ID_FIELD_NAME, qb)
	//						.query();
	//			}
	//			catch(SQLException e) {
	//				e.printStackTrace();
	//			}		
	//			return groupItems;
	//		}
	//		
	//		/**
	//		 * 
	//		 * @param cg
	//		 * @return
	//		 */
	//		public List<LootGroupHasItem> getItemsByGroup(LootContainerHasGroup cg) {
	//			// inner join to get groups
	//			List<LootGroupHasItem> groupItems = null;
	//			try {
	//
	//				// outer join to groups-items
	//				QueryBuilder<LootGroupHasItem, String> qb2 = groupItemDao.queryBuilder();
	//				groupItems = qb2
	////						.selectColumns(LootGroupHasItem.ITEM_ID_FIELD_NAME)
	//						.where()
	//						.eq(LootGroupHasItem.GROUP_ID_FIELD_NAME, cg.getGroup().getId())
	//						.query();
	//			}
	//			catch(SQLException e) {
	//				e.printStackTrace();
	//			}		
	//			return groupItems;
	//		}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return the server
	 */
//	public Server getServer() {
//		return server;
//	}

	/**
	 * @param server the server to set
	 */
//	public void setServer(Server server) {
//		this.server = server;
//	}

	/**
	 * @return the connSource
	 */
	public JdbcConnectionSource getConnSource() {
		return connSource;
	}

	/**
	 * @param connSource the connSource to set
	 */
	public void setConnSource(JdbcConnectionSource connSource) {
		this.connSource = connSource;
	}

	public JdbcConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(JdbcConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}
}

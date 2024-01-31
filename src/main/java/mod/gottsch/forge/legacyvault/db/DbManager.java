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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
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

	public static final String DB_FILE_NAME = "vault";
	public static final String DB_EXTENSION = ".mv.db";
	private static DbManager instance;
	// JPA
	private Connection connection;
	private Server server;

	/*
	 * In order to use ORM Lite, you must use a proprietary Connection object, 
	 * hence there will be two connections to use the H2 database.
	 */
	// ORM Lite
	private JdbcConnectionSource connSource;
	private Dao<Account, String> accountDao; 
	//		private Dao<LootContainerHasGroup, String> containerGroupDao;
	//		private Dao<LootGroupHasItem, String> groupItemDao;

	/**
	 *
	 * Private constructor
	 */
	private DbManager() throws ClassNotFoundException, SQLException {
		LOGGER.info("Initializing DbManager...");
		Connection conn = null;
		JdbcConnectionSource connSource = null;

		// load the driver class
		Class.forName("org.h2.Driver");
		LOGGER.info("loaded h2 driver...");

		// TODO move the rest of this stuff to startServer(), move stuff in startServer to createServer();
		// start h2 tcp server to allow connections from 3rd party clients while game is running.
		Server server = Server.createTcpServer().start();
		setServer(server);
		LOGGER.info("started tcp server...");

		// get the path to the default style sheet
		@SuppressWarnings("static-access")
		Path dbPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MODID, DB_FILE_NAME).toAbsolutePath();
		LOGGER.debug("path to db folder -> {}", dbPath.toString());

		// create the connection
		String databaseUrl = String.format("jdbc:h2:tcp://localhost:9092/%s;USER=%s;PASSWORD=%s;", dbPath.toString(), ServerConfig.DATABASE.user.get(), ServerConfig.DATABASE.password.get());
//		String databaseUrl = "jdbc:h2:tcp://localhost:9092/" +dbPath.toString() + ";USER=sa;PASSWORD=sa;";
		LOGGER.debug("db url -> {}", databaseUrl);
		conn = DriverManager.getConnection(databaseUrl);

		if (conn == null) {
			LOGGER.warn("Unable to connect JPA to h2 database.");
			return;
		}

		// set the connection
		setConnection(conn);

		// look for file --> vault.mv.db
		Path dbFilePath = Paths.get(dbPath.toString() + DB_EXTENSION);
		LOGGER.debug("path to db file -> {}", dbFilePath.toString());
		boolean pathExists =
				Files.exists(dbFilePath,
						new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
		LOGGER.debug("path exists -> {}", pathExists);

		// execute the script
		LOGGER.info("executing sql scripts...");
		try {
			// open a stream to the sql file
			@SuppressWarnings("static-access")
			String sqlScriptFilePath = "/" + LegacyVault.MODID + ".sql";
			LOGGER.debug("script path -> {}", sqlScriptFilePath.toString());
			InputStream is = getClass().getResourceAsStream(sqlScriptFilePath.toString());

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

			// OLD WAY
			//		Statement stat = conn.createStatement();
			//		stat.execute("runscript from 'classpath:/com/someguyssoftware/lootbuilder/db/treasure.sql'");
			//		stat.execute("runscript from 'classpath:/legacyvault.sql'");
		}
		catch(SQLException e) {
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

	/**
	 * 
	 * @return
	 */
	public static synchronized DbManager getInstance() {
		return instance;
	}

	public static void start() throws DbInitializationException {
		if (instance == null) {
			LOGGER.debug("Creating new instance of DbManager");
			try {
				instance = new DbManager();
			} catch (ClassNotFoundException | SQLException e) {
				LegacyVault.LOGGER.error("An error occurred during initialization ->", e);
				throw new DbInitializationException("Unable to create an instance of DbManager.");
			}			
		}		
	}

	/**
	 * 
	 */
	public static void shutdown() {
		getInstance().getConnSource().closeQuietly();
		getInstance().getServer().stop();
		instance = null;
		LOGGER.info("Shut down of DbManager complete.");
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
	public Server getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(Server server) {
		this.server = server;
	}

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
}

/**
 * 
 */
package com.revolut.transfers.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

/**
 * @author Dhawal Patel
 * 
 *         This will create required tables for the application.
 * 
 *         The database used is in memory Maria DB.
 *
 */
public class DatabaseManagerImpl implements DatabaseManager {

	private static final String DATABASE_NAME = "revolut";

	private static final String DATABASE_USER = "root";

	private final DBConfiguration config;

	private final DB database;

	public DatabaseManagerImpl() throws Exception {
		DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
		configBuilder.setPort(3310);
		configBuilder.setSecurityDisabled(false);
		config = configBuilder.build();
		database = createDatabase(config);
		createTables(database);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(config.getURL(DATABASE_NAME), "root", "");
	}

	@Override
	public void shutDownDB() throws Exception {
		this.database.stop();
	}

	private void createTables(DB database) throws SQLException, ManagedProcessException {
		database.source("build_tables.sql", DATABASE_USER, "", DATABASE_NAME);
	}

	private DB createDatabase(final DBConfiguration config) throws Exception {
		DB database = DB.newEmbeddedDB(config);
		database.start();
		database.createDB(DATABASE_NAME, DATABASE_USER, "");
		return database;
	}
}

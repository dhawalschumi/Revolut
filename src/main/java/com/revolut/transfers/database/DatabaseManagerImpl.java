/**
 * 
 */
package com.revolut.transfers.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Dhawal Patel
 * 
 *         This will create required tables for the application.
 * 
 *         The database used is in memory Maria DB.
 *
 */
@Slf4j
public class DatabaseManagerImpl implements DatabaseManager {

	private static final String DATABASE_NAME = "revolut";

	private static final String DATABASE_USER = "root";

	private final DBConfiguration config;

	private final DB database;

	private final PoolingDataSource<PoolableConnection> dataSource;

	public DatabaseManagerImpl() throws Exception {
		DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
		configBuilder.setPort(3310);
		configBuilder.setSecurityDisabled(false);
		config = configBuilder.build();
		database = createDatabase(config);
		createTables(database);
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(config.getURL(DATABASE_NAME),
				DATABASE_USER, new char[] {});
		PoolableConnectionFactory pooledConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
		GenericObjectPoolConfig<PoolableConnection> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(100);
		poolConfig.setMinIdle(50);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnCreate(true);
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(pooledConnectionFactory, poolConfig);
		pooledConnectionFactory.setPool(connectionPool);
		dataSource = new PoolingDataSource<>(connectionPool);
		warmup();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
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

	private void warmup() throws Exception {
		log.info("Warming Up Database connection");
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement("select name from customer where customer_id = ?");
			statement.setLong(1, 1000l);
			statement.executeQuery();
		} catch (Exception e) {
			log.error("Exception occured while warming up database connections", e);
			throw e;
		} finally {
			if (Objects.nonNull(statement)) {
				statement.close();
			}

			if (Objects.nonNull(connection)) {
				connection.close();
			}
		}
	}
}

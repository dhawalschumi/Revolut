/**
 * 
 */
package com.revolut.transfers.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dhawp
 *
 */
@Slf4j
public class DatabaseManagerImpl implements DatabaseManager {

	private final DBConfiguration config;

	public DatabaseManagerImpl() throws Exception {
		DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
		configBuilder.setPort(3306);
		configBuilder.setSecurityDisabled(false);
		config = configBuilder.build();
		createDatabase(config);
		createTables();
	}

	private void createDatabase(final DBConfiguration config) throws Exception {
		DB database = DB.newEmbeddedDB(config);
		database.start();
		database.createDB(DATABASE_NAME, "root", "");
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(config.getURL(DATABASE_NAME), "root", "");
	}

	private void createTables() throws SQLException {
		QueryRunner runner = new QueryRunner();
		Connection connection = getConnection();
		try {

			runner.update(connection,
					"CREATE TABLE CUSTOMER (customer_id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(200) NOT NULL, PRIMARY KEY ( customer_id ))");
			
		} catch (SQLException e) {
			log.error("Exception occured while creating CUSTOMER table");
			throw e;
		} finally {
			DbUtils.closeQuietly(connection);
		}

		connection = getConnection();
		try {
			runner.update(connection,
					"CREATE TABLE ACCOUNT (customer_id BIGINT NOT NULL, account_id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY ( account_id ), "
							+ "CONSTRAINT fk_customerid FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id))");
		} catch (SQLException e) {
			log.error("Exception occured while creating ACCOUNT table");
			throw e;
		} finally {
			DbUtils.closeQuietly(connection);
		}

		connection = getConnection();
		try {
			runner.update(getConnection(),
					"CREATE TABLE BALANCE (account_id BIGINT NOT NULL, balance DECIMAL(13,4) DEFAULT 0, "
							+ "CONSTRAINT fk_accountid FOREIGN KEY (account_id) REFERENCES ACCOUNT(account_id))");
		} catch (SQLException e) {
			log.error("Exception occured while creating BALANCE table");
			throw e;
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	@Override
	public void shutDownDB() throws Exception {

	}

}

package com.revolut.transfers.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Dhawal Patel
 *
 */
public interface DatabaseManager {

	public Connection getConnection() throws SQLException;

	public void shutDownDB() throws Exception;

}

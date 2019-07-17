package com.revolut.transfers.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseManager {

	public static final String DATABASE_NAME = "revolutdb";

	public Connection getConnection() throws SQLException;

	public void shutDownDB() throws Exception;

}

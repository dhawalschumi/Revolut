/**
 * 
 */
package com.revolut.query.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.revolut.transfers.account.Account;
import com.revolut.transfers.account.Balance;
import com.revolut.transfers.database.DatabaseManager;
import com.revolut.transfers.exception.BalanceNotFoundException;
import com.revolut.transfers.exception.ConcurrentUpdateException;
import com.revolut.transfers.exception.CustomerDoesNotExistsException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Dhawal Patel
 * 
 */
@Slf4j
@AllArgsConstructor
public class QueryServiceImpl implements QueryService {

	private final DatabaseManager databaseManager;

	@Override
	public Balance executeGetBalanceQuery(final long accountId) throws Exception {
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			connection = databaseManager.getConnection();
			statement = connection.prepareStatement("select amount, version from balance where account_id = ?");
			statement.setLong(1, accountId);
			ResultSet resultSet = statement.executeQuery();
			boolean isRecordPresent = resultSet.first();
			if (!isRecordPresent) {
				throw new BalanceNotFoundException("Balance for this customer does not exist");
			}
			BigDecimal amount = resultSet.getBigDecimal(1);
			long version = resultSet.getLong(2);
			return new Balance(amount, version);
		} catch (Exception e) {
			log.error("Exception occured while fetching balance for account {} ", accountId, e);
			throw e;
		} finally {
			cleanup(statement, connection);
		}
	}

	/*
	 * updateAccountBalance will be using optimistic updates. This will use record
	 * version technique where each where versions will be checked for the each
	 * update.
	 * 
	 * Updates will be done in batch mode which will help in implementing ALL or
	 * NONE mechanism.
	 * 
	 * Reason to choose this approach
	 * 
	 * Pros -
	 * 
	 * 1. No resource(rows or table) locks are required.
	 * 
	 * 2. Failures does not require to be complex logic to roll back. No need to
	 * configure database lock timeouts etc.
	 * 
	 * 3. Logic remains independent of underlying storage (using MySQL Postgres or
	 * using NoSQL like DynamoDB wont have impact).
	 * 
	 * 4. Provides more robustness to system and larger performance window compared
	 * to pessimistic locking
	 * 
	 * Cons -
	 * 
	 * 1. Chances of failure are more if same destination and source accounts are
	 * updated in within some milliseconds. But what are chances of happening the
	 * same is debatable?
	 * 
	 * 2. Testing is complex.
	 * 
	 * 3. In case of partial failures there are chances of having bad records.
	 */

	@Override
	public void updateAccountBalance(final Account fromAccount, final Account toAccount, final BigDecimal amount)
			throws Exception {
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			connection = databaseManager.getConnection();
			connection.setAutoCommit(false);
			statement = getTransferPreparedStatement(fromAccount, toAccount, amount, connection);
			int[] updateResult = statement.executeBatch();
			if (1 == updateResult[0] && 1 == updateResult[1]) {
				connection.commit();
				log.info("Fund Transfer from Account {} to Account {} is successful", fromAccount.getAccountId(),
						toAccount.getAccountId());
			} else {
				throw new ConcurrentUpdateException("Fund Transfer Failed due to version mismatch");
			}
			log.info("Fund Transfer from Account {} to Account {} is successful", fromAccount.getAccountId(),
					toAccount.getAccountId());
		} catch (Exception e) {
			log.error("Roll Back! Fund Transfer from Account {} to Account {} is failed", fromAccount.getAccountId(),
					toAccount.getAccountId(), e);
			connection.rollback();
			throw e;
		} finally {
			cleanup(statement, connection);
		}
	}

	private PreparedStatement getTransferPreparedStatement(final Account fromAccount, final Account toAccount,
			final BigDecimal amount, Connection connection) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("update balance set amount = ? , version = ? where account_id = ? and version = ?");
		statement.setBigDecimal(1, fromAccount.getAccountBalance().getAmount().subtract(amount));
		statement.setLong(2, fromAccount.getAccountBalance().getVersion() + 1);
		statement.setLong(3, fromAccount.getAccountId());
		statement.setLong(4, fromAccount.getAccountBalance().getVersion());
		statement.addBatch();
		statement.setBigDecimal(1, toAccount.getAccountBalance().getAmount().add(amount));
		statement.setLong(2, toAccount.getAccountBalance().getVersion() + 1);
		statement.setLong(3, toAccount.getAccountId());
		statement.setLong(4, toAccount.getAccountBalance().getVersion());
		statement.addBatch();
		return statement;
	}

	@Override
	public Account executeGetAccountForCustomer(long customerId) throws Exception {
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			connection = databaseManager.getConnection();
			statement = connection.prepareStatement("select account_id from account where customer_id = ?");
			statement.setLong(1, customerId);
			ResultSet result = statement.executeQuery();
			boolean recordExists = result.first();
			if (!recordExists) {
				throw new CustomerDoesNotExistsException("Customer " + customerId + " does not exists");
			}
			long accountId = result.getLong(1);
			Balance accountBalance = executeGetBalanceQuery(accountId);
			return new Account(accountId, accountBalance);
		} catch (Exception e) {
			log.error("Exception occured while fetching account for customer {} ", customerId, e);
			throw e;
		} finally {
			cleanup(statement, connection);
		}
	}

	private void cleanup(PreparedStatement statement, Connection connection) throws SQLException {
		if (Objects.nonNull(statement)) {
			statement.close();
		}
		if (Objects.nonNull(connection)) {
			connection.close();
		}
	}

}
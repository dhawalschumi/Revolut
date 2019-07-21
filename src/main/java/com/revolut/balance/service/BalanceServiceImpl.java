package com.revolut.balance.service;

import java.math.BigDecimal;

import com.revolut.query.service.QueryService;
import com.revolut.transfers.account.Account;
import com.revolut.transfers.account.Balance;

import lombok.AllArgsConstructor;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
public class BalanceServiceImpl implements BalanceService {

	private final QueryService queryService;

	@Override
	public Balance getAccountBalance(long accountId) throws Exception {
		return queryService.executeGetBalanceQuery(accountId);
	}

	@Override
	public void updateAccountBalance(final Account fromAccount, final Account toAccount, final BigDecimal amount)
			throws Exception {
		queryService.updateAccountBalance(fromAccount, toAccount, amount);
	}

}

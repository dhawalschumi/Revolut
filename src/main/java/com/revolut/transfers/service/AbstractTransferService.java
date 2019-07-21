/**
 * 
 */
package com.revolut.transfers.service;

import java.math.BigDecimal;

import com.revolut.account.service.AccountService;
import com.revolut.balance.service.BalanceService;
import com.revolut.transfers.account.Account;

import lombok.AllArgsConstructor;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
public abstract class AbstractTransferService implements TransferService {

	private final BalanceService balanceService;

	private final AccountService accountService;

	protected Account getAccount(final long customerId) throws Exception {
		return accountService.getAccountByCustomerId(customerId);
	}

	protected boolean isSufficientBalancePresent(final BigDecimal amount, final BigDecimal balanceAmount) {
		return balanceAmount.compareTo(amount) >= 0;
	}

	protected void transferAmount(final Account fromAccount, final Account toAccount, final BigDecimal transferAmount)
			throws Exception {
		balanceService.updateAccountBalance(fromAccount, toAccount, transferAmount);
	}

}

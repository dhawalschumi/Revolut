/**
 * 
 */
package com.revolut.transfers.service;

import com.revolut.account.service.AccountService;
import com.revolut.balance.service.BalanceService;
import com.revolut.handlers.request.TransfersRequest;
import com.revolut.transfers.account.Account;
import com.revolut.transfers.exception.InsufficientBalanceException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dhawal Patel
 *
 */
@Slf4j
public class TransferServiceImpl extends AbstractTransferService {

	public TransferServiceImpl(final BalanceService balanceService, final AccountService accountService) {
		super(balanceService, accountService);
	}

	@Override
	public Account transfer(final TransfersRequest request) throws Exception {
		Account fromAccount = null;
		try {
			long fromCustomerId = request.getFromCustomerId();
			long toCustomerId = request.getToCustomerId();
			log.info("Initiating Fund Transfer from account {} to account {}", fromCustomerId, toCustomerId);
			fromAccount = getAccount(fromCustomerId);
			if (!isSufficientBalancePresent(request.getTransferAmount(), fromAccount.getAccountBalance().getAmount())) {
				StringBuilder builder = new StringBuilder("Account ").append(fromCustomerId)
						.append(" does not have sufficient funds to transfer amount ")
						.append(fromAccount.getAccountBalance().getAmount().doubleValue());
				throw new InsufficientBalanceException(builder.toString());
			}
			Account toAccount = getAccount(toCustomerId);
			transferAmount(fromAccount, toAccount, request.getTransferAmount());
			fromAccount = getAccount(fromCustomerId);
		} catch (Exception e) {
			log.error("Exception Occured while transferring funds from account {} to account {}",
					request.getFromCustomerId(), request.getToCustomerId(), e);
			throw e;
		}

		return fromAccount;
	}

}

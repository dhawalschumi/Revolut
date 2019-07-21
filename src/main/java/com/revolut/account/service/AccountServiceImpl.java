/**
 * 
 */
package com.revolut.account.service;

import com.revolut.query.service.QueryService;
import com.revolut.transfers.account.Account;

import lombok.AllArgsConstructor;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final QueryService queryService;

	@Override
	public Account getAccountByCustomerId(final long customerId) throws Exception {
		return queryService.executeGetAccountForCustomer(customerId);
	}

}

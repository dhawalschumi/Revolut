/**
 * 
 */
package com.revolut.transfers.service;

import com.revolut.handlers.request.TransfersRequest;
import com.revolut.transfers.account.Account;

/**
 * @author Dhawal Patel
 * 
 *         This service will take care of transfers between two accounts.
 */
public interface TransferService {

	public Account transfer(final TransfersRequest request) throws Exception;

}

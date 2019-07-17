/**
 * 
 */
package com.revolut.handlers.request;

import java.math.BigDecimal;

import com.revolut.transfers.account.Account;

import lombok.Data;

/**
 * @author dhawp
 *
 */

@Data
public class TransfersRequest {

	private Account fromAccount;

	private Account toAccount;

	private BigDecimal amount;
}

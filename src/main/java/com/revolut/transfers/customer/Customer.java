/**
 * 
 */
package com.revolut.transfers.customer;

import com.revolut.transfers.account.Account;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author dhawp
 *
 */
@AllArgsConstructor
@Value
public class Customer {

	private final String customerId;

	private final Account account;

	private final String customerName;

}

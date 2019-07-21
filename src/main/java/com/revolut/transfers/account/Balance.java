/**
 * 
 */
package com.revolut.transfers.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
@Data
public class Balance {

	private final BigDecimal amount;

	private final long version;

}

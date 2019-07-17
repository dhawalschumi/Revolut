/**
 * 
 */
package com.revolut.transfers.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dhawp
 *
 */
@AllArgsConstructor
@Data
public class Balance {

	private final BigDecimal accountBalance;

}

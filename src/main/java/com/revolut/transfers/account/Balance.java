/**
 * 
 */
package com.revolut.transfers.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Balance {

	private BigDecimal amount;

	private long version;

}

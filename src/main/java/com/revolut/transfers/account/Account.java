package com.revolut.transfers.account;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Account {

	private final long accountId;

	private final Balance accountBalance;

}

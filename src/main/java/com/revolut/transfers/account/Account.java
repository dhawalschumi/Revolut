package com.revolut.transfers.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {

	private long accountId;

	private Balance accountBalance;

}

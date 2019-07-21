package com.revolut.handlers.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
@Data
public class ValidationError {

	private final String errorMessage;

	private final String errorCode;
}

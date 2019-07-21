package com.revolut.handlers.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dhawal Patel
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransfersRequest implements Request {

	private long fromCustomerId;

	private long toCustomerId;

	private BigDecimal transferAmount;

	private List<ValidationError> errors;

	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();
		if (toCustomerId <= 0) {
			errors.add(new ValidationError("Incorrect fromAccountId", "BAD_REQUEST"));
		}

		if (fromCustomerId <= 0) {
			errors.add(new ValidationError("Incorrect toAccountId", "BAD_REQUEST"));
		}

		if (fromCustomerId == toCustomerId) {
			errors.add(new ValidationError("Provide different fromCustomerId and toCustomerId", "BAD_REQUEST"));
		}

		if (BigDecimal.ZERO.compareTo(transferAmount) >= 0) {
			errors.add(new ValidationError("Incorrect transferAmount", "BAD_REQUEST"));
		}
		this.errors = errors;
		return errors;
	}

	public void addError(final ValidationError error) {
		if (CollectionUtils.isEmpty(errors)) {
			errors = new ArrayList<>();
		}
		errors.add(error);
	}
}

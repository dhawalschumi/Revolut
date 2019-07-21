/**
 * 
 */
package com.revolut.handlers.request;

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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDetailsRequest implements Request {

	private long customerId;

	private List<ValidationError> errors;

	@Override
	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();
		if (customerId <= 0) {
			errors.add(new ValidationError("Incorrect Customer Id Input", "BAD_REQUEST"));
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

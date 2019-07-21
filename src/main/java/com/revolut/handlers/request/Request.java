package com.revolut.handlers.request;

import java.util.List;

public interface Request {

	public List<ValidationError> validate();

}

package com.revolut.handlers;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.handlers.request.Request;
import com.revolut.handlers.request.ValidationError;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.TypedData;

/**
 * @author Dhawal Patel
 *
 */
public abstract class BaseHandler implements Handler {

	protected <T extends Request> T getRequestFromBody(final Context ctx, final TypedData body, final Class<T> clazz)
			throws Exception {
		ObjectMapper mapper = ctx.get(ObjectMapper.class);
		return mapper.readValue(body.getText(), clazz);
	}

	protected List<ValidationError> validate(final Request request) {
		return request.validate();
	}
}

/**
 * 
 */
package com.revolut.handlers;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.account.service.AccountService;
import com.revolut.handlers.request.AccountDetailsRequest;
import com.revolut.handlers.request.ValidationError;
import com.revolut.transfers.account.Account;

import lombok.AllArgsConstructor;
import ratpack.handling.Context;
import ratpack.http.Status;

/**
 * @author Dhawal Patel
 *
 */
@AllArgsConstructor
public class AccountDetailsHandler extends BaseHandler {

	private final AccountService accountService;

	@Override
	public void handle(Context ctx) throws Exception {
		ctx.getRequest().getBody().then(body -> {
			AccountDetailsRequest request = getRequestFromBody(ctx, body, AccountDetailsRequest.class);
			long customerId = request.getCustomerId();
			ObjectMapper mapper = ctx.get(ObjectMapper.class);
			try {
				List<ValidationError> errors = validate(request);
				if (!CollectionUtils.isEmpty(errors)) {
					ctx.getResponse().status(Status.BAD_REQUEST);
					ctx.render(mapper.writeValueAsString(request));
				} else {
					Account account = accountService.getAccountByCustomerId(customerId);
					ctx.render(mapper.writeValueAsString(account));
				}
			} catch (Exception e) {
				ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR);
				request.addError(new ValidationError("Unknown Error occured while fetching accounts", "UnknownError"));
				ctx.render(mapper.writeValueAsString(request));
			}
		});
	}

}

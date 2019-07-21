package com.revolut.handlers;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.handlers.request.TransfersRequest;
import com.revolut.handlers.request.ValidationError;
import com.revolut.transfers.account.Account;
import com.revolut.transfers.exception.ConcurrentUpdateException;
import com.revolut.transfers.exception.CustomerDoesNotExistsException;
import com.revolut.transfers.exception.InsufficientBalanceException;
import com.revolut.transfers.service.TransferService;

import lombok.extern.slf4j.Slf4j;
import ratpack.handling.Context;
import ratpack.http.Status;

/**
 * @author Dhawal Patel
 * 
 *         Ratpack Handler for handling transfer requests.
 **/
@Slf4j
public class TransfersHandler extends BaseHandler {

	@Override
	public void handle(Context ctx) throws Exception {
		ctx.getRequest().getBody().then(body -> {
			TransfersRequest request = getRequestFromBody(ctx, body, TransfersRequest.class);
			TransferService transferService = ctx.get(TransferService.class);
			ObjectMapper mapper = ctx.get(ObjectMapper.class);
			try {
				List<ValidationError> errors = validate(request);
				if (!CollectionUtils.isEmpty(errors)) {
					ctx.getResponse().status(Status.BAD_REQUEST);
					ctx.render(mapper.writeValueAsString(request));
				} else {
					Account fromAccount = transferService.transfer(request);
					ctx.render(mapper.writeValueAsString(fromAccount));
				}
			} catch (InsufficientBalanceException e) {
				logError(request, e);
				ctx.getResponse().status(Status.BAD_REQUEST);
				request.addError(new ValidationError("Insufficient Balance", "InsufficientBalance"));
				ctx.render(mapper.writeValueAsString(request));
			} catch (ConcurrentUpdateException e) {
				logError(request, e);
				request.addError(new ValidationError("Concurrent Update Observed", "ConcurrentUpdate"));
				ctx.getResponse().status(Status.CONFLICT);
				ctx.render(mapper.writeValueAsString(request));
			} catch (CustomerDoesNotExistsException e) {
				logError(request, e);
				request.addError(new ValidationError("Customer Does not Found", "CustomerNotFound"));
				ctx.getResponse().status(Status.BAD_REQUEST);
				ctx.render(mapper.writeValueAsString(request));
			} catch (Exception e) {
				logError(request, e);
				request.addError(new ValidationError("Unknown Error", "Unknown"));
				ctx.getResponse().status(Status.INTERNAL_SERVER_ERROR);
				ctx.render(mapper.writeValueAsString(request));
			}
		});
	}

	private void logError(TransfersRequest request, Exception e) {
		log.error("Exception Occured while transferring from account {} to account {}", request.getFromCustomerId(),
				request.getToCustomerId(), e);
	}
}

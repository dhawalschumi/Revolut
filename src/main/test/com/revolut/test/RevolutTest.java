/**
 * 
 */
package com.revolut.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.revolut.handlers.request.AccountDetailsRequest;
import com.revolut.handlers.request.TransfersRequest;
import com.revolut.transfers.RevolutApplication;
import com.revolut.transfers.account.Account;

import ratpack.http.client.ReceivedResponse;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

/**
 * @author Dhawal Patel
 *
 */
@RunWith(JUnit4.class)
public class RevolutTest {

	private static MainClassApplicationUnderTest application = new MainClassApplicationUnderTest(
			RevolutApplication.class);

	private static TestHttpClient testClient;

	@BeforeClass
	public static void init() {
		testClient = application.getHttpClient();
	}

	@AfterClass
	public static void destroy() {
		application.close();
	}

	private final ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
			.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
			.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));

	@Test
	public void testTransferSuccessCase() throws Exception {
		AccountDetailsRequest fromAccountDetailsRequest = new AccountDetailsRequest(1001, null);
		AccountDetailsRequest toAccountDetailsRequest = new AccountDetailsRequest(1003, null);
		TransfersRequest transferRequest = new TransfersRequest(1001, 1003, BigDecimal.valueOf(100.91d), null);

		/*
		 * Get From Account Details
		 */
		ReceivedResponse fromAccountDetailsResponse = testClient.request("accountDetails", request -> {
			request.post().body(body -> {
				body.text(mapper.writeValueAsString(fromAccountDetailsRequest));
			});
		});

		validateResponse(fromAccountDetailsResponse);

		/*
		 * Get To Account Details
		 */
		ReceivedResponse toAccountDetailsResponse = testClient.request("accountDetails", request -> {
			request.post().body(body -> {
				body.text(mapper.writeValueAsString(toAccountDetailsRequest));
			});
		});

		validateResponse(toAccountDetailsResponse);

		/*
		 * Transfer amount between from and to accounts
		 */
		ReceivedResponse response = testClient.request("transfer", request -> {
			request.post().body(body -> {
				System.out.println(mapper.writeValueAsString(transferRequest));
				body.text(mapper.writeValueAsString(transferRequest));
			});
		});

		validateResponse(response);

		Account fromAccountAfterTransfer = mapper.readValue(response.getBody().getText(), Account.class);

		/*
		 * Get To Account Details
		 */
		ReceivedResponse toAccountDetailsResponseAfterTransfer = testClient.request("accountDetails", request -> {
			request.post().body(body -> {
				body.text(mapper.writeValueAsString(toAccountDetailsRequest));
			});
		});

		validateResponse(toAccountDetailsResponseAfterTransfer);

		Account fromAccountBeforeTransfer = mapper.readValue(fromAccountDetailsResponse.getBody().getText(),
				Account.class);

		Account toAccountAfterTransfer = mapper.readValue(toAccountDetailsResponse.getBody().getText(), Account.class);

		Account toAccountDetailsBeforeTransfer = mapper
				.readValue(toAccountDetailsResponseAfterTransfer.getBody().getText(), Account.class);

		BigDecimal fromAccountAmountBeforeTransfer = fromAccountBeforeTransfer.getAccountBalance().getAmount()
				.subtract(transferRequest.getTransferAmount());

		BigDecimal toAccountAmountBeforeTransfer = toAccountDetailsBeforeTransfer.getAccountBalance().getAmount()
				.subtract(transferRequest.getTransferAmount());

		assertTrue("Excpected Balance after transfer operation is incorrect", fromAccountAmountBeforeTransfer
				.compareTo(fromAccountAfterTransfer.getAccountBalance().getAmount()) == 0);

		assertTrue("Excpected Balance after transfer operation is incorrect",
				toAccountAmountBeforeTransfer.compareTo(toAccountAfterTransfer.getAccountBalance().getAmount()) == 0);
	}

	private void validateResponse(ReceivedResponse response) {
		assertNotNull("Received response is null", response);
		assertNotNull("Received response body is null", response.getBody());
		assertTrue("Response code is not 200", 200 == response.getStatus().getCode());
	}

	@Test
	public void testTransferBadRequestCase() throws Exception {
		ReceivedResponse response = testClient.request("transfer", request -> {
			request.post().getBody()
					.text(mapper.writeValueAsString(new TransfersRequest(1002, 1001, new BigDecimal(0.0), null)));
		});
		assertNotNull("Received response is null", response);
		assertNotNull("Received response body is null", response.getBody());
		assertTrue("Response code is not 400 for BadRequest", 400 == response.getStatus().getCode());
	}

	@Test
	public void testTransferAccountNotFoundCase() throws Exception {
		ReceivedResponse response = testClient.request("transfer", request -> {
			request.post().getBody()
					.text(mapper.writeValueAsString(new TransfersRequest(100000, 1001, new BigDecimal(11.0), null)));
		});
		assertNotNull("Received response is null", response);
		assertNotNull("Received response body is null", response.getBody());
		assertTrue("Response code is not 400 for BadRequest", 400 == response.getStatus().getCode());
	}

	@Test
	public void testAccountDetailsSuccess() throws Exception {
		ReceivedResponse response = testClient.request("accountDetails", request -> {
			request.post().body(body -> {
				body.text(mapper.writeValueAsString(new AccountDetailsRequest(1005, null)));
			});
		});
		validateResponse(response);
		Account account = mapper.readValue(response.getBody().getText(), Account.class);
		assertTrue("Balance Does Not Match DB Value",
				BigDecimal.valueOf(7865785678.00).compareTo(account.getAccountBalance().getAmount()) == 0);
	}
}

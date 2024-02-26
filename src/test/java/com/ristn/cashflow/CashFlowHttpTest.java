package com.ristn.cashflow;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CashFlowHttpTest {
	private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CashFlowHttpTest.class.getName());
	private final String GOOD_URL = "http://localhost:5004/api/putExpenseRule";
	private final String BAD_URL = "http://localhost:5004/api/badEndpoint";
	private final OkHttpClient okHttpClient = new OkHttpClient();
	private final String expenseRule =
        """
			{
			    "name": "Mortgage",
			    "amount": -7667,
			    "onDate": 2,
			    "typeOfDay": "Non-holiday weekday after",
			    "freq": "Monthly",
			    "startDate": null,
			    "endDate": null,
			    "account": "S Chk"
			}
		""";

	private final String badExpenseRule =
			"""
				{
					"onDate": 2,
					"typeOfDay": "Non-holiday weekday after",
					"freq": "Monthly",
					"startDate": null,
					"endDate": null,
					"account": "S Chk"
				}
			""";

	@Test
	public void testPutExpenseRules() {
		RequestBody requestBody = RequestBody.create(expenseRule, MediaType.parse("application/json"));

		Request request = new Request.Builder()
				.url(GOOD_URL)
				.put(requestBody)
				.build();

		log.info(request.toString());

		try(Response response = okHttpClient.newCall(request).execute()) {
			log.info("Response: " + response);

			if (!response.isSuccessful()) {
				Assertions.fail("Failed to insert expense rule.");
			} else {
				log.info("Message: " + Objects.requireNonNull(response.body())
				                              .string());
			}
		}
		catch (NullPointerException | IOException e) {
			Assertions.fail(e);
		}
	}

	@Test
	public void testBadPutExpenseRules() {
		RequestBody requestBody = RequestBody.create(badExpenseRule, MediaType.parse("application/json"));

		Request request = new Request.Builder()
				.url(GOOD_URL)
				.put(requestBody)
				.build();

		log.info(request.toString());

		try(Response response = okHttpClient.newCall(request).execute()) {
			log.info("Response: " + response);

			if (response.isSuccessful()) {
				Assertions.fail("Should not have inserted expense rule.");
			}
		}
		catch (IOException e) {
			Assertions.fail(e);
		}
	}

	@Test
	public void testInvalidPutExpenseRulesEndpoint() {
		RequestBody requestBody = RequestBody.create(expenseRule, MediaType.parse("application/json"));

		Request request = new Request.Builder()
				.url(BAD_URL)
				.put(requestBody)
				.build();

		log.info(request.toString());

		try(Response response = okHttpClient.newCall(request).execute()) {
			log.info("Response: " + response);

			if (response.isSuccessful()) {
				Assertions.fail("Should not have inserted expense rule.");
			}
		}
		catch (IOException e) {
			Assertions.fail(e);
		}
	}
}

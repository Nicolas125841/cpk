## Comunication Contract

### How to programatically REQUEST data:

This microservice inserts an expense rule into a expenseRules MongoDB collection.

#### [PUT] :5004/api/putExpenseRule: 
This endpoint accepts a PUT request with a JSON body that contains the object representaiton
of the expense rule. 

This body MUST represents the DB schema: 

```
{
  name: { 
    type: String,
    required: true
  },
  account: { 
    type: String,
    required: true
  },
  amount: { 
    type: Number,
    required: true
  },
  freq: { 
    type: String,
    required: true
  },
  onDate: Number,
  typeOfDay: String,
  every: Number,
  startDate: String,
  endDate: String,
}
```

These fields are populated as follows:
- name (required): The name of the expense rule
- account (required): Which bank account this rule applies to
- amount (required): How much this adds/subtracts from the account
- frequency (required): The rate at which this rule is applied
- onDate: The date (month included depending on freq) which the rule is applied on
- typeOfDay: The type of day the rule is applied on
- every: The frequency of the rule application
- startDate: The beginning of when the rule applies
- endDate: The end of when the rule applies

Non-required fields to not have to be supplied in the JSON body. An example request body is:

```
{
    'name': 'Mortgage',
    'amount': -7667,
    'onDate': 2,
    'typeOfDay': 'Non-holiday weekday after',
    'freq': 'Monthly',
    'startDate': null,
    'endDate': null,
    'account': 'S Chk'
}
```

To make a request to this server, make a PUT request to 
/api/putExpenseRule with a JSON body containing a JSON object following the 
above schema. Here is an example program doing that:

```java
public class EndpointTest {
	private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(EndpointTest.class.getName());
	private final String URL = "http://localhost:5004/api/putExpenseRule";
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
	
	public static void main(String[] args) {
		RequestBody requestBody = RequestBody.create(expenseRule, MediaType.parse("application/json"));

		Request request = new Request.Builder()
				.url(URL)
				.put(requestBody)
				.build();

		log.info(request.toString());

		try(Response response = okHttpClient.newCall(request).execute()) {
			log.info("Response: " + response);

			if (response.isSuccessful()) {
				log.info("Message: " + Objects.requireNonNull(response.body())
				                              .string());
			} else {
				log.info("ERROR");
			}
		}
		catch (NullPointerException | IOException e) {
			throw new RuntimeException(e);
		}
    }
}
```

### How to programatically RECEIVE data:

Since this microservice focuses only on inserting client data, it only returns response codes/messages
related to the status of inserting that data. 

The possible responses are:

- Code 200, Response {"message":"Expense rule saved to expense rules list"}: This response code means that the 
  expense rule was successfully parsed and stored in the DB
- Code 404: This response means that the endpoint wasn't specified correctly, most likely a typo
- Code 500: This response means that the request was somehow malformed, either missing data or improperly formatted. 
  It could also mean there is some server error with MongoDB

These response data can be accessed from the .code() (status code response field) method in OkHttp, and if the code 
is 200, the message can be verified by parsing the response body as a string or JSON.

- Code parse: `response.code()` > Int
- Message parse: `response.body().string()` > String

### UML Sequence Diagram

![Diagram](/UML/SequenceDiagram.PNG)
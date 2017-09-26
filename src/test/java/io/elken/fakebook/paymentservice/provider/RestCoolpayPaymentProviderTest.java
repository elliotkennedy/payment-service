package io.elken.fakebook.paymentservice.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.elken.fakebook.paymentservice.domain.Currency;
import io.elken.fakebook.paymentservice.domain.LoginRequest;
import io.elken.fakebook.paymentservice.domain.LoginResponse;
import io.elken.fakebook.paymentservice.domain.Payment;
import io.elken.fakebook.paymentservice.domain.Recipient;
import io.elken.fakebook.paymentservice.domain.Status;
import io.elken.fakebook.paymentservice.exception.ServiceException;
import io.elken.fakebook.paymentservice.exception.UnauthorizedException;
import io.elken.fakebook.paymentservice.provider.api.coolpay.PaymentWrapper;
import io.elken.fakebook.paymentservice.provider.api.coolpay.PaymentsResponse;
import io.elken.fakebook.paymentservice.provider.api.coolpay.RecipientWrapper;
import io.elken.fakebook.paymentservice.provider.api.coolpay.RecipientsResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.DefaultRequestExpectation;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

public class RestCoolpayPaymentProviderTest {

	private static final String COOLPAY_BASE_URI = "https://coolpay.com/api";

	private static final String COOLPAY_TOKEN = "someToken";
	private static final String COOLPAY_TOKEN_HEADER = "Bearer " + COOLPAY_TOKEN;

	private MockRestServiceServer mockCoolpayServer;
	private ObjectMapper objectMapper;

	private RestCoolpayPaymentProvider onTest;

	@Before
	public void setup() throws Exception {
		objectMapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();
		mockCoolpayServer = MockRestServiceServer.createServer(restTemplate);

		onTest = new RestCoolpayPaymentProvider(restTemplate, COOLPAY_BASE_URI);
	}

	@Test
	public void testLogin() throws Exception {
		LoginRequest loginRequest = new LoginRequest("user", "apikey");
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/login"))
				.andExpect(method(POST))
				.andExpect(header(CONTENT_TYPE, APPLICATION_JSON_VALUE))
				.andExpect(content().string(objectMapper.writeValueAsString(loginRequest)))
				.andRespond(withSuccess(objectMapper.writeValueAsString(new LoginResponse(COOLPAY_TOKEN)), APPLICATION_JSON));

		LoginResponse response = onTest.login(loginRequest);

		assertThat(response.getToken()).isEqualTo(COOLPAY_TOKEN);
	}

	@Test(expected = UnauthorizedException.class)
	public void testLogin_whenLoginFails() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/login"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

		onTest.login(new LoginRequest("user", "apikey"));
	}

	@Test(expected = ServiceException.class)
	public void testLogin_whenCoolpayFails() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/login"))
				.andRespond(withServerError());

		onTest.login(new LoginRequest("user", "apikey"));
	}

	@Test
	public void testListRecipients() throws Exception {
		Recipient recipient = new Recipient("1", "Joe Cool");
		RecipientsResponse recipientsResponse = new RecipientsResponse(Collections.singletonList(recipient));

		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/recipients"))
				.andExpect(method(GET))
				.andExpect(header(CONTENT_TYPE, APPLICATION_JSON_VALUE))
				.andExpect(header("Authorization", COOLPAY_TOKEN_HEADER))
				.andRespond(withSuccess(objectMapper.writeValueAsString(recipientsResponse), APPLICATION_JSON));

		List<Recipient> recipients = onTest.listRecipients(COOLPAY_TOKEN);

		assertThat(recipients).hasSize(1);
		assertThat(recipients.get(0).getId()).isEqualTo("1");
	}

	@Test(expected = UnauthorizedException.class)
	public void testListRecipients_whenTokenNull() throws Exception {
		onTest.listRecipients(null);
	}

	@Test(expected = UnauthorizedException.class)
	public void testListRecipients_whenTokenEmpty() throws Exception {
		onTest.listRecipients("   ");
	}

	@Test(expected = UnauthorizedException.class)
	public void testListRecipients_whenUnauthorized() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/recipients"))
				.andRespond(withUnauthorizedRequest());

		onTest.listRecipients(COOLPAY_TOKEN);
	}

	@Test(expected = ServiceException.class)
	public void testListRecipients_whenServerError() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/recipients"))
				.andRespond(withServerError());

		onTest.listRecipients(COOLPAY_TOKEN);
	}

	@Test
	public void testCreateRecipient() throws Exception {
		Recipient recipientResponse = new Recipient("1", "Jim Beam");

		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/recipients"))
				.andExpect(method(POST))
				.andExpect(header(CONTENT_TYPE, APPLICATION_JSON_VALUE))
				.andExpect(header("Authorization", COOLPAY_TOKEN_HEADER))
				.andRespond(withSuccess(objectMapper.writeValueAsString(new RecipientWrapper(recipientResponse)), APPLICATION_JSON));

		Recipient recipient = onTest.createRecipient(COOLPAY_TOKEN, new Recipient(null, "Jim Beam"));

		assertThat(recipient).isNotNull();
		assertThat(recipient.getId()).isEqualTo("1");
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreateRecipient_whenNullToken() throws Exception {
		onTest.createRecipient(null, new Recipient(null, "Jim Beam"));
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreateRecipient_whenEmptyToken() throws Exception {
		onTest.createRecipient("       ", new Recipient(null, "Jim Beam"));
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreateRecipient_whenUnauthorized() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/recipients"))
				.andRespond(withUnauthorizedRequest());

		onTest.createRecipient(COOLPAY_TOKEN, new Recipient(null, "Jim Beam"));
	}

	@Test(expected = ServiceException.class)
	public void testCreateRecipient_whenServerError() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/recipients"))
				.andRespond(withServerError());

		onTest.createRecipient(COOLPAY_TOKEN, new Recipient(null, "Jim Beam"));
	}

	@Test
	public void testListPayments() throws Exception {
		Payment payment = new Payment("1", Status.paid, "recipient", Currency.EUR, 10.09);
		PaymentsResponse paymentsResponse = new PaymentsResponse(Collections.singletonList(payment));

		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/payments"))
				.andExpect(method(GET))
				.andExpect(header(CONTENT_TYPE, APPLICATION_JSON_VALUE))
				.andExpect(header("Authorization", COOLPAY_TOKEN_HEADER))
				.andRespond(withSuccess(objectMapper.writeValueAsString(paymentsResponse), APPLICATION_JSON));

		List<Payment> payments = onTest.listPayments(COOLPAY_TOKEN);

		assertThat(payments).hasSize(1);
		assertThat(payments.get(0).getId()).isEqualTo("1");
	}

	@Test(expected = UnauthorizedException.class)
	public void testListPayments_whenTokenNull() throws Exception {
		onTest.listPayments(null);
	}

	@Test(expected = UnauthorizedException.class)
	public void testListPayments_whenTokenEmpty() throws Exception {
		onTest.listPayments("   ");
	}

	@Test(expected = UnauthorizedException.class)
	public void testListPayments_whenUnauthorized() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/payments"))
				.andRespond(withUnauthorizedRequest());

		onTest.listPayments(COOLPAY_TOKEN);
	}

	@Test(expected = ServiceException.class)
	public void testListPayments_whenServerError() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/payments"))
				.andRespond(withServerError());

		onTest.listPayments(COOLPAY_TOKEN);
	}

	@Test
	public void testCreatePayment() throws Exception {
		Payment paymentResponse = payment("1");

		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/payments"))
				.andExpect(method(POST))
				.andExpect(header(CONTENT_TYPE, APPLICATION_JSON_VALUE))
				.andExpect(header("Authorization", COOLPAY_TOKEN_HEADER))
				.andRespond(withSuccess(objectMapper.writeValueAsString(new PaymentWrapper(paymentResponse)), APPLICATION_JSON));

		Payment payment = onTest.createPayment(COOLPAY_TOKEN, payment(null));

		assertThat(payment).isNotNull();
		assertThat(payment.getId()).isEqualTo("1");
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreatePayment_whenNullToken() throws Exception {
		onTest.createPayment(null, payment(null));
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreatePayment_whenEmptyToken() throws Exception {
		onTest.createPayment("       ", payment(null));
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreatePayment_whenUnauthorized() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/payments"))
				.andRespond(withUnauthorizedRequest());

		onTest.createPayment(COOLPAY_TOKEN, payment(null));
	}

	@Test(expected = ServiceException.class)
	public void testCreatePayment_whenServerError() throws Exception {
		mockCoolpayServer.expect(requestTo(COOLPAY_BASE_URI + "/payments"))
				.andRespond(withServerError());

		onTest.createPayment(COOLPAY_TOKEN, payment(null));
	}

	private Payment payment(String id) {
		return new Payment(id, Status.paid, "recipient", Currency.EUR, 10.99);
	}

}

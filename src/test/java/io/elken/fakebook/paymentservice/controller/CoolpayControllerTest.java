package io.elken.fakebook.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.elken.fakebook.paymentservice.domain.Currency;
import io.elken.fakebook.paymentservice.domain.LoginRequest;
import io.elken.fakebook.paymentservice.domain.LoginResponse;
import io.elken.fakebook.paymentservice.domain.Payment;
import io.elken.fakebook.paymentservice.domain.Recipient;
import io.elken.fakebook.paymentservice.domain.Status;
import io.elken.fakebook.paymentservice.exception.ServiceException;
import io.elken.fakebook.paymentservice.exception.UnauthorizedException;
import io.elken.fakebook.paymentservice.provider.PaymentProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CoolpayControllerTest {

	private static final String AUTH_TOKEN = "token";

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	private PaymentProvider paymentProvider;

	@Before
	public void setup() throws Exception {
		paymentProvider = mock(PaymentProvider.class);
		mockMvc = standaloneSetup(new CoolpayController(paymentProvider)).build();

		objectMapper = new ObjectMapper();
	}

	@Test
	public void testLogin() throws Exception {
		LoginRequest loginRequest = new LoginRequest("username", "password");

		LoginResponse expectedResponse = new LoginResponse("token");
		when(paymentProvider.login(eq(loginRequest))).thenReturn(expectedResponse);

		mockMvc.perform(post("/coolpay/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
	}

	@Test
	public void testLogin_unauthorizedWhenLoginFailed() throws Exception {
		when(paymentProvider.login(any(LoginRequest.class))).thenThrow(new UnauthorizedException());

		mockMvc.perform(post("/coolpay/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new LoginRequest("username", "pass"))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testLogin_badGatewayWhenServiceError() throws Exception {
		when(paymentProvider.login(any(LoginRequest.class))).thenThrow(new ServiceException());

		mockMvc.perform(post("/coolpay/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new LoginRequest("username", "pass"))))
				.andExpect(status().isBadGateway());
	}

	@Test
	public void testListRecipients() throws Exception {
		List<Recipient> recipients = Collections.singletonList(new Recipient("1", "Fred"));
		when(paymentProvider.listRecipients(eq(AUTH_TOKEN))).thenReturn(recipients);

		mockMvc.perform(get("/coolpay/recipients")
				.header("Authorization", AUTH_TOKEN))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(recipients)));
	}

	@Test
	public void testListRecipients_requiresAuthHeader() throws Exception {
		mockMvc.perform(get("/coolpay/recipients"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testListRecipients_unauthorizedWhenInvalidToken() throws Exception {
		when(paymentProvider.listRecipients(anyString())).thenThrow(new UnauthorizedException());

		mockMvc.perform(get("/coolpay/recipients")
				.header("Authorization", AUTH_TOKEN))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testListRecipients_badGatewayServiceError() throws Exception {
		when(paymentProvider.listRecipients(anyString())).thenThrow(new ServiceException());

		mockMvc.perform(get("/coolpay/recipients")
				.header("Authorization", AUTH_TOKEN))
				.andExpect(status().isBadGateway());
	}

	@Test
	public void testCreateRecipient() throws Exception {
		ArgumentCaptor<Recipient> captor = ArgumentCaptor.forClass(Recipient.class);
		Recipient response = new Recipient("1", "James");
		when(paymentProvider.createRecipient(eq(AUTH_TOKEN), any(Recipient.class))).thenReturn(response);

		mockMvc.perform(post("/coolpay/recipients")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", AUTH_TOKEN)
				.content(objectMapper.writeValueAsString(new Recipient(null, "James"))))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(paymentProvider).createRecipient(eq(AUTH_TOKEN), captor.capture());
		Recipient recipient = captor.getValue();
		assertThat(recipient).isNotNull();
		assertThat(recipient.getId()).isNull();
		assertThat(recipient.getName()).isEqualTo("James");
	}

	@Test
	public void testCreateRecipient_requiresAuthHeader() throws Exception {
		mockMvc.perform(post("/coolpay/recipients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new Recipient(null, "James"))))
				.andExpect(status().isBadRequest());
	}


	@Test
	public void testCreateRecipient_unauthorizedWhenInvalidToken() throws Exception {
		when(paymentProvider.createRecipient(anyString(), any(Recipient.class))).thenThrow(new UnauthorizedException());

		mockMvc.perform(post("/coolpay/recipients")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", AUTH_TOKEN)
				.content(objectMapper.writeValueAsString(new Recipient(null, "James"))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testCreateRecipient_badGatewayServiceError() throws Exception {
		when(paymentProvider.createRecipient(anyString(), any(Recipient.class))).thenThrow(new ServiceException());

		mockMvc.perform(post("/coolpay/recipients")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", AUTH_TOKEN)
				.content(objectMapper.writeValueAsString(new Recipient(null, "James"))))
				.andExpect(status().isBadGateway());
	}

	@Test
	public void testListPayments() throws Exception {
		Payment payment = new Payment("1", Status.paid, "recipient", Currency.GBP, 10.99);
		List<Payment> payments = Collections.singletonList(payment);
		when(paymentProvider.listPayments(eq(AUTH_TOKEN))).thenReturn(payments);

		mockMvc.perform(get("/coolpay/payments")
				.header("Authorization", AUTH_TOKEN))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(payments)));
	}

	@Test
	public void testListPayments_requiresAuthHeader() throws Exception {
		mockMvc.perform(get("/coolpay/payments"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testListPayments_unauthorizedWhenInvalidToken() throws Exception {
		when(paymentProvider.listPayments(anyString())).thenThrow(new UnauthorizedException());

		mockMvc.perform(get("/coolpay/payments")
				.header("Authorization", AUTH_TOKEN))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testListPayments_badGatewayServiceError() throws Exception {
		when(paymentProvider.listPayments(anyString())).thenThrow(new ServiceException());

		mockMvc.perform(get("/coolpay/payments")
				.header("Authorization", AUTH_TOKEN))
				.andExpect(status().isBadGateway());
	}


	@Test
	public void testCreatePayment() throws Exception {
		ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
		Payment response = new Payment("1", Status.paid, "recipient", Currency.GBP, 10.99);
		when(paymentProvider.createPayment(eq(AUTH_TOKEN), any(Payment.class))).thenReturn(response);

		mockMvc.perform(post("/coolpay/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", AUTH_TOKEN)
				.content(objectMapper.writeValueAsString(new Payment(null, Status.paid, "recipient", Currency.GBP, 10.99))))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(paymentProvider).createPayment(eq(AUTH_TOKEN), captor.capture());
		Payment payment = captor.getValue();
		assertThat(payment).isNotNull();
		assertThat(payment.getId()).isNull();
		assertThat(payment.getStatus()).isEqualTo(Status.paid);
		assertThat(payment.getRecipientId()).isEqualTo("recipient");
		assertThat(payment.getCurrency()).isEqualTo(Currency.GBP);
		assertThat(payment.getAmount()).isEqualTo(10.99);
	}

	@Test
	public void testCreatePayment_requiresAuthHeader() throws Exception {
		mockMvc.perform(post("/coolpay/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new Payment(null, Status.paid, "recipient", Currency.GBP, 10.99))))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreatePayment_unauthorizedWhenInvalidToken() throws Exception {
		when(paymentProvider.createPayment(anyString(), any(Payment.class))).thenThrow(new UnauthorizedException());

		mockMvc.perform(post("/coolpay/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", AUTH_TOKEN)
				.content(objectMapper.writeValueAsString(new Payment(null, Status.paid, "recipient", Currency.GBP, 10.99))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void testCreatePayment_badGatewayServiceError() throws Exception {
		when(paymentProvider.createPayment(anyString(), any(Payment.class))).thenThrow(new ServiceException());

		mockMvc.perform(post("/coolpay/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", AUTH_TOKEN)
				.content(objectMapper.writeValueAsString(new Payment(null, Status.paid, "recipient", Currency.GBP, 10.99))))
				.andExpect(status().isBadGateway());
	}

}

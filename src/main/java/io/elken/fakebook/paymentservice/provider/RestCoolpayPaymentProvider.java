package io.elken.fakebook.paymentservice.provider;

import io.elken.fakebook.paymentservice.domain.LoginRequest;
import io.elken.fakebook.paymentservice.domain.LoginResponse;
import io.elken.fakebook.paymentservice.domain.Payment;
import io.elken.fakebook.paymentservice.domain.Recipient;
import io.elken.fakebook.paymentservice.exception.UnauthorizedException;
import io.elken.fakebook.paymentservice.exception.ServiceException;
import io.elken.fakebook.paymentservice.provider.api.coolpay.PaymentWrapper;
import io.elken.fakebook.paymentservice.provider.api.coolpay.PaymentsResponse;
import io.elken.fakebook.paymentservice.provider.api.coolpay.RecipientWrapper;
import io.elken.fakebook.paymentservice.provider.api.coolpay.RecipientsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class RestCoolpayPaymentProvider implements PaymentProvider {

	private static final Logger log = LoggerFactory.getLogger(RestCoolpayPaymentProvider.class);

	private static final String TOKEN_FORMAT = "Bearer %s";

	private final RestTemplate restTemplate;
	private final String coolpayBaseUri;

	public RestCoolpayPaymentProvider(RestTemplate restTemplate, @Value("${coolpay.base.uri}") String coolpayBaseUri) {
		this.restTemplate = restTemplate;
		this.coolpayBaseUri = coolpayBaseUri;
	}

	@Override
	public LoginResponse login(LoginRequest loginRequest) {
		LinkedMultiValueMap<String, String> headers = getHeaders();
		HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
		try {
			return request("/login", POST, request, LoginResponse.class);
		} catch (HttpClientErrorException e) {
			if (NOT_FOUND.equals(e.getStatusCode())) {
				throw new UnauthorizedException();
			}
			throw e;
		}
	}

	@Override
	public List<Recipient> listRecipients(String token) throws UnauthorizedException {
		HttpEntity<Object> request = authorizedRequest(token);
		RecipientsResponse response = request("/recipients", GET, request, RecipientsResponse.class);
		return response.getRecipients();
	}

	@Override
	public Recipient createRecipient(String token, Recipient recipient) throws UnauthorizedException {
		HttpEntity<Object> request = authorizedRequest(token, new RecipientWrapper(recipient));
		RecipientWrapper response = request("/recipients", POST, request, RecipientWrapper.class);
		return response.getRecipient();
	}

	@Override
	public List<Payment> listPayments(String token) {
		HttpEntity<Object> request = authorizedRequest(token);
		PaymentsResponse response = request("/payments", GET, request, PaymentsResponse.class);
		return response.getPayments();
	}

	@Override
	public Payment createPayment(String token, Payment payment) {
		HttpEntity<Object> request = authorizedRequest(token, new PaymentWrapper(payment));
		PaymentWrapper response = request("/payments", POST, request, PaymentWrapper.class);
		return response.getPayment();
	}

	private HttpEntity<Object> authorizedRequest(String token, Object body) {
		if (token == null || token.trim().isEmpty()) {
			throw new UnauthorizedException();
		}
		Map<String, List<String>> headers = getHeaders();
		headers.put(HttpHeaders.AUTHORIZATION, singletonList(format(TOKEN_FORMAT, token)));
		return new HttpEntity<>(body, new LinkedMultiValueMap<>(headers));
	}

	private HttpEntity<Object> authorizedRequest(String token) {
		return authorizedRequest(token, null);
	}

	private LinkedMultiValueMap<String, String> getHeaders() {
		return new LinkedMultiValueMap<>(singletonMap(HttpHeaders.CONTENT_TYPE, singletonList(MediaType.APPLICATION_JSON_VALUE)));
	}

	private <T> T request(String urlPath, HttpMethod httpMethod, HttpEntity<?> request, Class<T> responseType) {
		try {
			ResponseEntity<T> response = restTemplate.exchange(coolpayBaseUri + urlPath, httpMethod, request, responseType);
			return response.getBody();
		} catch (HttpServerErrorException e) {
			log.error("Coolpay api returned server error status code.", e);
			throw new ServiceException(e);
		} catch (HttpClientErrorException e) {
			log.debug("Coolpay api returned client error status code.", e);
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				throw new UnauthorizedException();
			} else {
				throw e;
			}
		}
	}

}

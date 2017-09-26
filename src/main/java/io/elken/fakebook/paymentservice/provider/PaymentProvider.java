package io.elken.fakebook.paymentservice.provider;

import io.elken.fakebook.paymentservice.domain.LoginRequest;
import io.elken.fakebook.paymentservice.domain.LoginResponse;
import io.elken.fakebook.paymentservice.domain.Payment;
import io.elken.fakebook.paymentservice.domain.Recipient;

import java.util.List;

public interface PaymentProvider {

	LoginResponse login(LoginRequest loginRequest);
	List<Recipient> listRecipients(String token);
	Recipient createRecipient(String token, Recipient recipient);
	List<Payment> listPayments(String token);
	Payment createPayment(String token, Payment payment);

}

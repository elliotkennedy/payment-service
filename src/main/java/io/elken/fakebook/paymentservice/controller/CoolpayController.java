package io.elken.fakebook.paymentservice.controller;

import io.elken.fakebook.paymentservice.domain.LoginRequest;
import io.elken.fakebook.paymentservice.domain.LoginResponse;
import io.elken.fakebook.paymentservice.domain.Payment;
import io.elken.fakebook.paymentservice.domain.Recipient;
import io.elken.fakebook.paymentservice.provider.PaymentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/coolpay")
public class CoolpayController {

	private static final Logger log = LoggerFactory.getLogger(CoolpayController.class);

	private final PaymentProvider paymentProvider;

	public CoolpayController(PaymentProvider paymentProvider) {
		this.paymentProvider = paymentProvider;
	}

	@ResponseBody
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public LoginResponse login(@RequestBody LoginRequest loginRequest) {
		return paymentProvider.login(loginRequest);
	}

	@ResponseBody
	@RequestMapping(path = "/recipients", method = RequestMethod.GET)
	public List<Recipient> listRecipients(@RequestHeader(value = "Authorization") String authHeader) {
		return paymentProvider.listRecipients(authHeader);
	}

	@ResponseBody
	@RequestMapping(path = "/recipients", method = RequestMethod.POST)
	public Recipient createRecipient(@RequestHeader(value = "Authorization") String authHeader, @RequestBody Recipient recipient) {
		log.debug("Create recipient request '{}'", recipient);
		return paymentProvider.createRecipient(authHeader, recipient);
	}

	@ResponseBody
	@RequestMapping(path = "/payments", method = RequestMethod.GET)
	public List<Payment> listPayments(@RequestHeader(value = "Authorization") String authHeader) {
		return paymentProvider.listPayments(authHeader);
	}

	@ResponseBody
	@RequestMapping(path = "/payments", method = RequestMethod.POST)
	public Payment createPayment(@RequestHeader(value = "Authorization") String authHeader, @RequestBody Payment payment) {
		log.debug("Create payment request '{}'", payment);
		return paymentProvider.createPayment(authHeader, payment);
	}

}


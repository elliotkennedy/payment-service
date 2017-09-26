package io.elken.fakebook.paymentservice.provider.api.coolpay;

import io.elken.fakebook.paymentservice.domain.Payment;

public class PaymentWrapper {

	private Payment payment;

	public PaymentWrapper() {
	}

	public PaymentWrapper(Payment payment) {
		this.payment = payment;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@Override
	public String toString() {
		return "PaymentWrapper{" +
				"payment=" + payment +
				'}';
	}

}

package io.elken.fakebook.paymentservice.provider.api.coolpay;

import io.elken.fakebook.paymentservice.domain.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentsResponse {

	private List<Payment> payments = new ArrayList<>();

	public PaymentsResponse(List<Payment> payments) {
		this.payments = payments;
	}

	public PaymentsResponse() {
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	@Override
	public String toString() {
		return "PaymentsResponse{" +
				"payments=" + payments +
				'}';
	}

}

package io.elken.fakebook.paymentservice.provider.api.coolpay;

import io.elken.fakebook.paymentservice.domain.Recipient;

import java.util.ArrayList;
import java.util.List;

public class RecipientsResponse {

	private List<Recipient> recipients = new ArrayList<>();

	public RecipientsResponse(List<Recipient> recipients) {
		this.recipients = recipients;
	}

	public RecipientsResponse() {
	}

	public List<Recipient> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<Recipient> recipients) {
		this.recipients = recipients;
	}

	@Override
	public String toString() {
		return "RecipientsResponse{" +
				"recipients=" + recipients +
				'}';
	}

}

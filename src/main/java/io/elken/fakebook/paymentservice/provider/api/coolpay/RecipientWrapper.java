package io.elken.fakebook.paymentservice.provider.api.coolpay;

import io.elken.fakebook.paymentservice.domain.Recipient;

public class RecipientWrapper {

	private Recipient recipient;

	public RecipientWrapper() {
	}

	public RecipientWrapper(Recipient recipient) {
		this.recipient = recipient;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	@Override
	public String toString() {
		return "RecipientWrapper{" +
				"recipient=" + recipient +
				'}';
	}

}

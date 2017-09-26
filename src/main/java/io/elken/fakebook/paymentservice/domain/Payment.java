package io.elken.fakebook.paymentservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payment {

	private String id;
	private Status status;
	@JsonProperty("recipient_id")
	private String recipientId;
	private Currency currency;
	private double amount;

	public Payment() {
	}

	public Payment(String id, Status status, String recipientId, Currency currency, double amount) {
		this.id = id;
		this.status = status;
		this.recipientId = recipientId;
		this.currency = currency;
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Payment{" +
				"id='" + id + '\'' +
				", status=" + status +
				", recipientId='" + recipientId + '\'' +
				", currency=" + currency +
				", amount=" + amount +
				'}';
	}

}

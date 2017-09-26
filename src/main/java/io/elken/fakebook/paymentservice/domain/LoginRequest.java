package io.elken.fakebook.paymentservice.domain;

public class LoginRequest {

	private String username;
	private String apikey;

	public LoginRequest() {
	}

	public LoginRequest(String username, String apikey) {
		this.username = username;
		this.apikey = apikey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoginRequest that = (LoginRequest) o;

		if (username != null ? !username.equals(that.username) : that.username != null) return false;
		return apikey != null ? apikey.equals(that.apikey) : that.apikey == null;
	}

	@Override
	public int hashCode() {
		int result = username != null ? username.hashCode() : 0;
		result = 31 * result + (apikey != null ? apikey.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LoginRequest{" +
				"username='" + username + '\'' +
				", apikey='" + apikey + '\'' +
				'}';
	}

}

package com.eureka.auth.model;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {

	@SerializedName("message")
	private String mMessage;
	@SerializedName("status")
	private Long mStatus;
	@SerializedName("token")
	private Token mToken;

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		mMessage = message;
	}

	public Long getStatus() {
		return mStatus;
	}

	public void setStatus(Long status) {
		mStatus = status;
	}

	public Token getToken() {
		return mToken;
	}

	public void setToken(Token token) {
		mToken = token;
	}

}

package com.dashur.integration.commons.exception;

import lombok.Getter;

@Getter
public class CustomException extends BaseException {

	private String errCode;
	private String errDesc;

	public CustomException(String errCode) {
		super(Code.APPLICATION, "Unspecified Error");
		this.errCode = errCode;
	}

	public CustomException(String errCode, String errDesc) {
		super(Code.APPLICATION, "Unspecified Error");
		this.errCode = errCode;
		this.errDesc = errDesc;
	}
}
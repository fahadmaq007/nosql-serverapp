package com.maqbool.server.exception;

import java.text.MessageFormat;

/**
 * BusinessException represents the business scenario that needs notification to
 * the user.
 * 
 * @author Maqbool.Ahmed
 * 
 */
public class BusinessException extends ServiceException {

	private static final long serialVersionUID = -7657111991342365664L;

	private String detailedMessage;

	/**
	 * Constructs with the supplied error code & the detailed message.
	 * 
	 * @param message error code
	 * @param detailedMessage detailed message
	 */
	public BusinessException(String message,
			String detailedMessage) {
		super(message);
		this.detailedMessage = detailedMessage;
	}

	public BusinessException(String errorCodeType, Object ... params) {
		super(errorCodeType);
		String msg = errorCodeType;
		this.detailedMessage = MessageFormat.format(msg, params);
	}

	public String getDetailedMessage() {
		return detailedMessage;
	}
	
	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

}

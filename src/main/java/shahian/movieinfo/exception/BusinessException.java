package shahian.movieinfo.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final String errorCode;
	private final int statusCode;

	public BusinessException(String message) {
		super(message);
		this.errorCode = "BUSINESS_ERROR";
		this.statusCode = 400;
	}

	public BusinessException(String message, String errorCode, int statusCode) {
		super(message);
		this.errorCode = errorCode;
		this.statusCode = statusCode;
	}
}
package shahian.movieinfo.exception;

public class InvalidParameterException extends BusinessException {

	public InvalidParameterException(String parameterName, String reason) {
		super(
				String.format("Invalid parameter '%s': %s", parameterName, reason),
				"INVALID_PARAMETER",
				400
		);
	}
}
package shahian.movieinfo.exception;

public class DataProcessingException extends BusinessException {

	public DataProcessingException(String message) {
		super(message, "DATA_PROCESSING_ERROR", 500);
	}

	public DataProcessingException(String message, Throwable cause) {
		super(message, "DATA_PROCESSING_ERROR", 500);
		initCause(cause);
	}
}
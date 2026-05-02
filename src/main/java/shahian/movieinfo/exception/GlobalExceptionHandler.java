package shahian.movieinfo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	//  ResourceNotFoundException (404)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFound(
			ResourceNotFoundException ex, WebRequest request) {

		log.warn("Resource not found: {}", ex.getMessage());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", ex.getStatusCode());
		body.put("error", getErrorName(ex.getStatusCode()));
		body.put("message", ex.getMessage());
		body.put("path", getPath(request));
		body.put("errorCode", ex.getErrorCode());

		return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatusCode()));
	}

	// م InvalidParameterException (400)
	@ExceptionHandler(InvalidParameterException.class)
	public ResponseEntity<Object> handleInvalidParameter(
			InvalidParameterException ex, WebRequest request) {

		log.warn("Invalid parameter: {}", ex.getMessage());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", ex.getStatusCode());
		body.put("error", getErrorName(ex.getStatusCode()));
		body.put("message", ex.getMessage());
		body.put("path", getPath(request));
		body.put("errorCode", ex.getErrorCode());

		return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatusCode()));
	}

	//  DataProcessingException (500)
	@ExceptionHandler(DataProcessingException.class)
	public ResponseEntity<Object> handleDataProcessing(
			DataProcessingException ex, WebRequest request) {

		log.error("Data processing error: {}", ex.getMessage(), ex);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", ex.getStatusCode());
		body.put("error", getErrorName(ex.getStatusCode()));
		body.put("message", ex.getMessage());
		body.put("path", getPath(request));
		body.put("errorCode", ex.getErrorCode());

		return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatusCode()));
	}

	//  BusinessException
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusinessException(
			BusinessException ex, WebRequest request) {

		log.warn("Business exception: {}", ex.getMessage());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", ex.getStatusCode());
		body.put("error", getErrorName(ex.getStatusCode()));
		body.put("message", ex.getMessage());
		body.put("path", getPath(request));
		body.put("errorCode", ex.getErrorCode());

		return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatusCode()));
	}


	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {

		log.warn("Type mismatch: {}", ex.getMessage());

		String message = String.format("Parameter '%s' should be of type '%s'",
				ex.getName(),
				ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", 400);
		body.put("error", "Bad Request");
		body.put("message", message);
		body.put("path", getPath(request));
		body.put("errorCode", "TYPE_MISMATCH");

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(
			IllegalArgumentException ex, WebRequest request) {

		log.warn("Illegal argument: {}", ex.getMessage());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", 400);
		body.put("error", "Bad Request");
		body.put("message", ex.getMessage());
		body.put("path", getPath(request));
		body.put("errorCode", "ILLEGAL_ARGUMENT");

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	// م (500)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(
			Exception ex, WebRequest request) {

		log.error("Unexpected error occurred: ", ex);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", 500);
		body.put("error", "Internal Server Error");
		body.put("message", "An unexpected error occurred. Please try again later.");
		body.put("path", getPath(request));
		body.put("errorCode", "INTERNAL_SERVER_ERROR");

		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	//   WebRequest
	private String getPath(WebRequest request) {
		String description = request.getDescription(false);
		if (description != null && description.startsWith("uri=")) {
			return description.substring(4);
		}
		return description;
	}

	//   status code
	private String getErrorName(int statusCode) {
		return switch (statusCode) {
			case 400 -> "Bad Request";
			case 404 -> "Not Found";
			case 409 -> "Conflict";
			case 422 -> "Unprocessable Entity";
			default -> "Error";
		};
	}
}
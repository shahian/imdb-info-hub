package shahian.movieinfo.exception;

public class ResourceNotFoundException extends BusinessException {

	public ResourceNotFoundException(String resourceName, String identifier) {
		super(
				String.format("%s not found with identifier: %s", resourceName, identifier),
				"RESOURCE_NOT_FOUND",
				404
		);
	}

	public ResourceNotFoundException(String message) {
		super(message, "RESOURCE_NOT_FOUND", 404);
	}
}
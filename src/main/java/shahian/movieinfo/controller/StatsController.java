package shahian.movieinfo.controller;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shahian.movieinfo.config.RequestCountingFilter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

	private final RequestCountingFilter requestCountingFilter;

	@GetMapping("/requests")
	@Operation(
			summary = "Get total HTTP requests count since last startup",
			description = "Returns the total number of HTTP requests received by the application since the last startup."
	)
	public ResponseEntity<Map<String, Long>> getRequestCount() {
		long count = requestCountingFilter.getRequestCount();
		return ResponseEntity.ok(Map.of("requestCount", count));
	}
}
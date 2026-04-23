package shahian.movieinfo.controller;

import java.util.Map;

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
	public ResponseEntity<Map<String, Long>> getRequestCount() {
		long count = requestCountingFilter.getRequestCount();
		return ResponseEntity.ok(Map.of("requestCount", count));
	}
}
package shahian.movieinfo.controller;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shahian.movieinfo.model.dto.TitleDTO;
import shahian.movieinfo.service.GenreRankingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {

	private final GenreRankingService genreRankingService;

	@GetMapping("/{genre}/best-per-year")
	@Operation(
			summary = "Get best titles per year for a specific genre",
			description = "Returns up to 'limit' best titles for each year (default limit=10 per year). Rankings are based on rating and vote count using Bayesian average."
	)
	public ResponseEntity<Map<Integer, List<TitleDTO>>> getBestTitlesPerYear(
			@PathVariable String genre, @RequestParam(defaultValue = "10") int limit) {
		log.info("Received request for genre: {}", genre);
		Map<Integer, List<TitleDTO>> result = genreRankingService.getBestTitlesPerYearByGenre(genre, limit);
		return ResponseEntity.ok(result);
	}
}
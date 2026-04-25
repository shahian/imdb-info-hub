package shahian.movieinfo.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import shahian.movieinfo.model.Title;
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
public class GenreController {

	private final GenreRankingService genreRankingService;

	@GetMapping("/{genre}/best-per-year")
	public ResponseEntity<Map<Integer, List<Title>>> getBestTitlesPerYear(
			@PathVariable String genre,
			@RequestParam(defaultValue = "10") int limit) {
		Map<Integer, List<Title>> result = genreRankingService.getBestTitlesPerYearByGenre(genre, limit);
		return ResponseEntity.ok(result);
	}
}
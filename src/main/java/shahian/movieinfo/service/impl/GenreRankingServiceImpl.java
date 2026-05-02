package shahian.movieinfo.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import shahian.movieinfo.exception.InvalidParameterException;
import shahian.movieinfo.exception.ResourceNotFoundException;
import shahian.movieinfo.model.dto.TitleDTO;
import shahian.movieinfo.service.GenreRankingService;
import shahian.movieinfo.service.TitleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GenreRankingServiceImpl implements GenreRankingService {

	private final TitleService titleService;

	@Override
	public Map<Integer, List<TitleDTO>> getBestTitlesPerYearByGenre(String genre, int limitPerYear) {
		if (genre == null || genre.isBlank()) {
			throw new InvalidParameterException("genre", "Genre cannot be null or empty");
		}
		Map<Integer, List<TitleDTO>> result = new LinkedHashMap<>();
		List<Integer> years = getValidYears(genre);

		for (Integer year : years) {
			List<TitleDTO> bestTitles = titleService.getBestByGenreAndYearIMDB(
					genre, year, getMinVotes(), getGlobalAverageRating(), limitPerYear);
			if (bestTitles != null && !bestTitles.isEmpty()) {
				result.put(year, bestTitles);
			}
		}

		return result;
	}

	private @NonNull List<Integer> getValidYears(String genre) {
		int actualMinYear = titleService.getMinYear();
		int actualMaxYear = titleService.getMaxYear();
		List<Integer> validYears = titleService.getYearsByGenre(genre)
				.stream()
				.filter(Objects::nonNull)
				.filter(y -> y >= actualMinYear && y <= actualMaxYear)
				.toList();

		if (validYears.isEmpty()) {
			throw new ResourceNotFoundException("No titles found for genre: '" + genre + "'");
		}
		return validYears;
	}


	private double getGlobalAverageRating() {
		return Optional.of(titleService.getGlobalAverageRating())
				.filter(r -> r > 0)
				.orElse(5.0);
	}

	private int getMinVotes() {
		return Optional.of(titleService.getRecommendedMinVotes())
				.filter(v -> v > 0)
				.orElse(100);
	}

}
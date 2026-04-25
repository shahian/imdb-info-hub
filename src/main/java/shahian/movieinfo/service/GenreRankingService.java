package shahian.movieinfo.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shahian.movieinfo.model.Title;
import shahian.movieinfo.repository.TitleRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GenreRankingService {

	private final TitleRepository titleRepository;

	@Value("${imdb.ranking.min-votes:1000}")
	private int minVotes;

	private double weightedScore(Title title) {
		if (title.getRating() == null || title.getRating()
				.getNumVotes() < minVotes) {
			return 0;
		}
		double r = title.getRating()
				.getAverageRating();
		double v = title.getRating()
				.getNumVotes();
		return (r * v) / (v + minVotes);
	}

	public Map<Integer, List<Title>> getBestTitlesPerYearByGenre(String genre, int limitPerYear) {
		log.debug("Finding best titles per year for genre: {}, limit: {}", genre, limitPerYear);

		List<Title> allTitles = titleRepository.findByGenresContaining(genre);

		Map<Integer, List<Title>> byYear = new HashMap<>();
		for (Title t : allTitles) {
			Integer year = t.getStartYear();
			if (year == null || year <= 0) {
				continue;
			}
			if (t.getRating() != null && t.getRating()
					.getNumVotes() >= minVotes) {
				byYear.computeIfAbsent(year, k -> new ArrayList<>())
						.add(t);
			}
		}

		Map<Integer, List<Title>> result = new LinkedHashMap<>();
		for (Map.Entry<Integer, List<Title>> entry : byYear.entrySet()) {
			List<Title> sorted = entry.getValue()
					.stream()
					.sorted(Comparator.comparingDouble(this::weightedScore)
							.reversed())
					.limit(limitPerYear)
					.collect(Collectors.toList());
			result.put(entry.getKey(), sorted);
		}
		return result;
	}
}
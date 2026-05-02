package shahian.movieinfo.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shahian.movieinfo.exception.InvalidParameterException;
import shahian.movieinfo.exception.ResourceNotFoundException;
import shahian.movieinfo.model.Title;
import shahian.movieinfo.model.dto.TitleDTO;
import shahian.movieinfo.repository.TitleRepository;
import shahian.movieinfo.service.TitlePrincipalService;
import shahian.movieinfo.service.TitleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TitleServiceImpl implements TitleService {

	private final TitlePrincipalService titlePrincipalService;

	private final TitleRepository titleRepository;

	private static final int BATCH_SIZE = 200;

	@Override
	public List<TitleDTO> getTitlesSameDirectorWriterAlive() {
		log.debug("Finding titles with same director and writer who is alive");
		long start = System.currentTimeMillis();

		try {
			List<TitleDTO> result = titleRepository.findTitleDTOsWithSameDirectorWriterAlive();
			long elapsed = System.currentTimeMillis() - start;
			if (result == null || result.isEmpty()) {
				log.info("No titles found (took {} ms)", elapsed);
				throw new ResourceNotFoundException("No titles found where director and writer are the same person and still alive");
			}

			log.info("Found {} titles in {} ms", result.size(), elapsed);
			return result;

		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error fetching titles", e);
			throw new RuntimeException("Failed to fetch titles: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Title> getCommonTitlesForActors(String actor1Id, String actor2Id) {
		log.debug("Finding common titles for actors: {} and {}", actor1Id, actor2Id);

		validateActorId(actor1Id, "actor1Id");
		validateActorId(actor2Id, "actor2Id");

		if (actor1Id.equals(actor2Id)) {
			throw new InvalidParameterException("actor1Id, actor2Id",
					"Actor IDs must be different. Same actor cannot have common titles with themselves");
		}
		List<String> commonIds = titlePrincipalService.getCommonMovieIds(actor1Id, actor2Id);
		log.info("Found {} common movie IDs for actors {} and {}", commonIds.size(), actor1Id, actor2Id);
		List<Title> result = new ArrayList<>();
		for (int i = 0; i < commonIds.size(); i += BATCH_SIZE) {
			int end = Math.min(i + BATCH_SIZE, commonIds.size());
			List<String> batch = commonIds.subList(i, end);
			List<Title> batchResult = titleRepository.findAllById(batch);
			if (!batchResult.isEmpty()) {
				result.addAll(batchResult);
				log.info("Successfully returned {} common titles for actors {} and {}",
						result.size(), actor1Id, actor2Id);
			}
		}
		return result;
	}

	@Override
	public List<TitleDTO> getBestByGenreAndYearIMDB(String genre, Integer year, int minVotes, double globalAvgRating, int limitPerYear) {
		return titleRepository.findBestByGenreAndYearIMDB(genre, year, minVotes, globalAvgRating, limitPerYear);
	}

	@Override
	public Double getGlobalAverageRating() {

		return titleRepository.getGlobalAverageRating();
	}

	@Override
	public Integer getMinYear() {
		return titleRepository.getMinYear();
	}

	@Override
	public Integer getMaxYear() {
		return titleRepository.getMaxYear();
	}

	@Override
	public Integer getRecommendedMinVotes() {
		return titleRepository.getRecommendedMinVotes();
	}

	@Override
	public List<Integer> getYearsByGenre(String genre) {
		return titleRepository.findYearsByGenre(genre);
	}

	private void validateActorId(String actorId, String paramName) {
		if (actorId == null || actorId.trim()
				.isEmpty()) {
			throw new InvalidParameterException(paramName, "Actor ID cannot be null or empty");
		}

	}
}
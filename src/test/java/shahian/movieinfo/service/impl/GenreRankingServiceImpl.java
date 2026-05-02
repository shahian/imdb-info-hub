package shahian.movieinfo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shahian.movieinfo.exception.InvalidParameterException;
import shahian.movieinfo.exception.ResourceNotFoundException;
import shahian.movieinfo.model.dto.TitleDTO;
import shahian.movieinfo.service.TitleService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreRankingServiceImplTest {

	@Mock
	private TitleService titleService;

	@InjectMocks
	private GenreRankingServiceImpl genreRankingService;

	private TitleDTO sampleTitleDTO;

	@BeforeEach
	void setUp() {
		sampleTitleDTO = TitleDTO.builder()
				.tconst("tt0000001")
				.primaryTitle("Carmencita")
				.startYear(1894)
				.genres("Documentary,Short")
				.averageRating(5.7)
				.numVotes(2207)
				.build();
	}

	@Test
	void getBestTitlesPerYearByGenre_ShouldReturnRankedTitles_WhenGenreExists() {
		// Given
		String genre = "Documentary";
		int limit = 5;
		List<Integer> years = Arrays.asList(1894, 1895);
		List<TitleDTO> titles1894 = Arrays.asList(sampleTitleDTO);
		List<TitleDTO> titles1895 = Arrays.asList(
				TitleDTO.builder()
						.tconst("tt0000010")
						.primaryTitle("Leaving the Factory")
						.startYear(1895)
						.averageRating(6.8)
						.numVotes(8303)
						.build()
		);

		when(titleService.getYearsByGenre(genre)).thenReturn(years);
		when(titleService.getMinYear()).thenReturn(1894);
		when(titleService.getMaxYear()).thenReturn(2024);
		when(titleService.getGlobalAverageRating()).thenReturn(6.5);
		when(titleService.getRecommendedMinVotes()).thenReturn(100);
		when(titleService.getBestByGenreAndYearIMDB(eq(genre), eq(1894), anyInt(), anyDouble(), eq(limit)))
				.thenReturn(titles1894);
		when(titleService.getBestByGenreAndYearIMDB(eq(genre), eq(1895), anyInt(), anyDouble(), eq(limit)))
				.thenReturn(titles1895);

		// When
		Map<Integer, List<TitleDTO>> result = genreRankingService.getBestTitlesPerYearByGenre(genre, limit);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(1894)).hasSize(1);
		assertThat(result.get(1894).get(0).getPrimaryTitle()).isEqualTo("Carmencita");
	}

	@Test
	void getBestTitlesPerYearByGenre_ShouldThrowException_WhenGenreIsNull() {
		assertThatThrownBy(() -> genreRankingService.getBestTitlesPerYearByGenre(null, 10))
				.isInstanceOf(InvalidParameterException.class)
				.hasMessageContaining("Genre cannot be null");
	}

	@Test
	void getBestTitlesPerYearByGenre_ShouldThrowException_WhenGenreIsEmpty() {
		assertThatThrownBy(() -> genreRankingService.getBestTitlesPerYearByGenre("", 10))
				.isInstanceOf(InvalidParameterException.class)
				.hasMessageContaining("Genre cannot be null");
	}

	@Test
	void getBestTitlesPerYearByGenre_ShouldThrowException_WhenNoYearsFound() {
		String genre = "NonExistentGenre";

		when(titleService.getYearsByGenre(genre)).thenReturn(Arrays.asList());
		when(titleService.getMinYear()).thenReturn(1894);
		when(titleService.getMaxYear()).thenReturn(2024);

		assertThatThrownBy(() -> genreRankingService.getBestTitlesPerYearByGenre(genre, 10))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("No titles found for genre");
	}
}
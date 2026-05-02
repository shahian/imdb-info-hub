package shahian.movieinfo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shahian.movieinfo.exception.InvalidParameterException;
import shahian.movieinfo.exception.ResourceNotFoundException;
import shahian.movieinfo.model.Title;
import shahian.movieinfo.model.dto.TitleDTO;
import shahian.movieinfo.repository.TitleRepository;
import shahian.movieinfo.service.TitlePrincipalService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TitleServiceImplTest {

	@Mock
	private TitleRepository titleRepository;

	@Mock
	private TitlePrincipalService titlePrincipalService;

	@InjectMocks
	private TitleServiceImpl titleService;

	private Title sampleTitle;
	private TitleDTO sampleTitleDTO;

	@BeforeEach
	void setUp() {
		sampleTitle = new Title();
		sampleTitle.setTconst("tt0000001");
		sampleTitle.setPrimaryTitle("Carmencita");
		sampleTitle.setStartYear(1894);
		sampleTitle.setGenres("Documentary,Short");

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
	void getTitlesSameDirectorWriterAlive_ShouldReturnTitles_WhenFound() {
		// Given
		List<TitleDTO> expectedTitles = Arrays.asList(sampleTitleDTO);
		when(titleRepository.findTitleDTOsWithSameDirectorWriterAlive()).thenReturn(expectedTitles);

		// When
		List<TitleDTO> result = titleService.getTitlesSameDirectorWriterAlive();

		// Then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getPrimaryTitle()).isEqualTo("Carmencita");
	}

	@Test
	void getTitlesSameDirectorWriterAlive_ShouldThrowException_WhenNoTitlesFound() {
		when(titleRepository.findTitleDTOsWithSameDirectorWriterAlive()).thenReturn(Arrays.asList());

		assertThatThrownBy(() -> titleService.getTitlesSameDirectorWriterAlive())
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("No titles found");
	}

	@Test
	void getCommonTitlesForActors_ShouldReturnTitles_WhenActorsHaveCommonMovies() {
		// Given
		String actor1Id = "nm0000001";
		String actor2Id = "nm0000002";
		List<String> commonIds = Arrays.asList("tt0000001", "tt0000002");
		List<Title> expectedTitles = Arrays.asList(sampleTitle);

		when(titlePrincipalService.getCommonMovieIds(actor1Id, actor2Id)).thenReturn(commonIds);
		when(titleRepository.findAllById(commonIds)).thenReturn(expectedTitles);

		// When
		List<Title> result = titleService.getCommonTitlesForActors(actor1Id, actor2Id);

		// Then
		assertThat(result).hasSize(1);
	}

	@Test
	void getCommonTitlesForActors_ShouldThrowException_WhenActorIdIsNull() {
		assertThatThrownBy(() -> titleService.getCommonTitlesForActors(null, "nm0000002"))
				.isInstanceOf(InvalidParameterException.class)
				.hasMessageContaining("Actor ID cannot be null");
	}

	@Test
	void getCommonTitlesForActors_ShouldThrowException_WhenSameActorId() {
		String actorId = "nm0000001";

		assertThatThrownBy(() -> titleService.getCommonTitlesForActors(actorId, actorId))
				.isInstanceOf(InvalidParameterException.class)
				.hasMessageContaining("Actor IDs must be different");
	}
}
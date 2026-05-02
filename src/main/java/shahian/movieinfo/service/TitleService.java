package shahian.movieinfo.service;

import java.util.List;

import shahian.movieinfo.model.Title;
import shahian.movieinfo.model.dto.TitleDTO;

public interface TitleService {
	List<TitleDTO> getTitlesSameDirectorWriterAlive();

	List<Title> getCommonTitlesForActors(String actor1Id, String actor2Id);

	List<TitleDTO> getBestByGenreAndYearIMDB(String genre, Integer year, int minVotes, double globalAvgRating, int limitPerYear);

	Double getGlobalAverageRating();

	Integer getMinYear();

	Integer getMaxYear();

	Integer getRecommendedMinVotes();

	List<Integer> getYearsByGenre(String genre);

}

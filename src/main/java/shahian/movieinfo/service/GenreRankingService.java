package shahian.movieinfo.service;

import java.util.List;
import java.util.Map;

import shahian.movieinfo.model.dto.TitleDTO;

public interface GenreRankingService {
	Map<Integer, List<TitleDTO>> getBestTitlesPerYearByGenre(String genre, int limit);
}

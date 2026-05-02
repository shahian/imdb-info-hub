package shahian.movieinfo.service;

import java.util.List;

public interface TitlePrincipalService {

	List<String> getCommonMovieIds(String actor1Id, String actor2Id);
}

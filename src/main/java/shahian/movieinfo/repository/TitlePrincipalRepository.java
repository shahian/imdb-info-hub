package shahian.movieinfo.repository;

import java.util.List;
import shahian.movieinfo.model.TitlePrincipal;
import shahian.movieinfo.model.TitlePrincipalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TitlePrincipalRepository extends JpaRepository<TitlePrincipal, TitlePrincipalId> {

	@Query("SELECT DISTINCT tp1.id.tconst FROM TitlePrincipal tp1, TitlePrincipal tp2 " +
			"WHERE tp1.id.tconst = tp2.id.tconst " +
			"AND tp1.nconst = :actor1Id AND tp1.category IN ('actor', 'actress') " +
			"AND tp2.nconst = :actor2Id AND tp2.category IN ('actor', 'actress')")
	List<String> findCommonMovieIds(@Param("actor1Id") String actor1Id,
			@Param("actor2Id") String actor2Id);
}
package shahian.movieinfo.repository;

import shahian.movieinfo.model.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title, String> {

	Optional<Title> findByTconst(String tconst);

	List<Title> findByGenresContaining(String genre);

	List<Title> findByStartYear(Integer year);

	@Query("SELECT t FROM Title t WHERE t.genres LIKE %:genre% AND t.startYear BETWEEN :startYear AND :endYear")
	List<Title> findByGenreAndYearRange(@Param("genre") String genre,
			@Param("startYear") Integer startYear,
			@Param("endYear") Integer endYear);

	@Query("SELECT t FROM Title t JOIN t.rating r WHERE r.numVotes >= :minVotes ORDER BY r.averageRating DESC")
	List<Title> findTopRatedTitles(@Param("minVotes") Integer minVotes);
}
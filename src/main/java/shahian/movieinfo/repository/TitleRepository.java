package shahian.movieinfo.repository;

import java.util.List;

import shahian.movieinfo.model.Title;
import shahian.movieinfo.model.dto.TitleDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRepository extends JpaRepository<Title, String> {

	@Query(value = "SELECT DISTINCT t.start_year FROM title_basics t " +
			"WHERE t.genres LIKE CONCAT('%', :genre, '%') " +
			"AND t.start_year > 0 " +
			"ORDER BY t.start_year",
			nativeQuery = true)
	List<Integer> findYearsByGenre(@Param("genre") String genre);

	@Query(value = "SELECT COALESCE(AVG(average_rating), 5.0) FROM title_ratings", nativeQuery = true)
	Double getGlobalAverageRating();

	@Query(value = "SELECT MIN(start_year) FROM title_basics WHERE start_year > 0", nativeQuery = true)
	Integer getMinYear();

	@Query(value = "SELECT MAX(start_year) FROM title_basics", nativeQuery = true)
	Integer getMaxYear();

	@Query(value = "SELECT PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY num_votes) FROM title_ratings", nativeQuery = true)
	Integer getRecommendedMinVotes();


	@Query(value = "SELECT DISTINCT t.tconst as tconst, t.primary_title as primaryTitle, " +
			"t.start_year as startYear, t.genres as genres, " +
			"r.average_rating as averageRating, r.num_votes as numVotes " +
			"FROM title_basics t " +
			"LEFT JOIN title_ratings r ON t.tconst = r.tconst " +
			"INNER JOIN title_crew tc ON t.tconst = tc.tconst " +
			"WHERE tc.directors = tc.writers " +
			"AND tc.directors IS NOT NULL " +
			"AND tc.directors != '' " +
			"AND tc.directors IN (SELECT n.nconst FROM name_basics n WHERE n.death_year IS NULL)",
			nativeQuery = true)
	List<TitleDTO> findTitleDTOsWithSameDirectorWriterAlive();

	@Query(value = "SELECT t.tconst as tconst, t.primary_title as primaryTitle, " +
			"t.start_year as startYear, t.genres as genres, " +
			"COALESCE(r.average_rating, 0) as averageRating, " +
			"COALESCE(r.num_votes, 0) as numVotes " +
			"FROM title_basics t " +
			"LEFT JOIN title_ratings r ON t.tconst = r.tconst " +
			"WHERE t.genres LIKE CONCAT('%', :genre, '%') " +
			"AND t.start_year = :year " +
			"AND (r.num_votes IS NULL OR r.num_votes >= :minVotes) " +
			"ORDER BY " +
			"CASE WHEN r.num_votes IS NULL OR r.num_votes = 0 THEN 0 " +
			"ELSE (r.num_votes * 1.0 / (r.num_votes + :minVotes)) * r.average_rating + " +
			"(:minVotes * 1.0 / (r.num_votes + :minVotes)) * :globalAvgRating " +
			"END DESC " +
			"LIMIT :limit",
			nativeQuery = true)
	List<TitleDTO> findBestByGenreAndYearIMDB(@Param("genre") String genre,
			@Param("year") Integer year,
			@Param("minVotes") int minVotes,
			@Param("globalAvgRating") double globalAvgRating,
			@Param("limit") int limit);
}
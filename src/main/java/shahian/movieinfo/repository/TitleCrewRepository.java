package shahian.movieinfo.repository;

import java.util.List;

import shahian.movieinfo.model.TitleCrew;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleCrewRepository extends JpaRepository<TitleCrew, String> {

	@Query("SELECT tc FROM TitleCrew tc WHERE tc.directors = tc.writers")
	List<TitleCrew> findWhereDirectorEqualsWriter();

	@Query("SELECT tc FROM TitleCrew tc WHERE tc.directors LIKE %:nconst% AND tc.writers LIKE %:nconst%")
	List<TitleCrew> findByBothDirectorAndWriter(@Param("nconst") String nconst);

}
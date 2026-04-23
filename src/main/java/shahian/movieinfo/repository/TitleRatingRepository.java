package shahian.movieinfo.repository;

import java.util.List;

import shahian.movieinfo.model.TitleRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRatingRepository extends JpaRepository<TitleRating, String> {

	List<TitleRating> findByNumVotesGreaterThanEqualOrderByAverageRatingDesc(int minVotes);
}

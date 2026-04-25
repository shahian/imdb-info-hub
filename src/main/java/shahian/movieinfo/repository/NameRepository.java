package shahian.movieinfo.repository;

import java.util.List;

import shahian.movieinfo.model.Name;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NameRepository extends JpaRepository<Name, String> {

	List<Name> findByDeathYearIsNull();
}
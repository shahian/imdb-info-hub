package shahian.movieinfo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "name_basics", indexes = {
		@Index(name = "idx_name_nconst", columnList = "nconst"),
		@Index(name = "idx_name_primary_name", columnList = "primaryName"),
		@Index(name = "idx_name_death_year", columnList = "deathYear")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Name {

	@Id
	@Column(name = "nconst", length = 20, nullable = false)
	private String nconst;

	@Column(name = "primary_name", length = 500)
	private String primaryName;

	@Column(name = "birth_year")
	private Integer birthYear;

	@Column(name = "death_year")
	private Integer deathYear;

	@Column(name = "primary_profession", columnDefinition = "TEXT")
	private String primaryProfession;

	@Column(name = "known_for_titles", columnDefinition = "TEXT")
	private String knownForTitles;

	@OneToMany(mappedBy = "name", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<TitlePrincipal> principals = new ArrayList<>();

}
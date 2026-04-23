package shahian.movieinfo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

	@Column(name = "primary_profession", length = 500)
	private String primaryProfession;

	@Column(name = "known_for_titles", length = 500)
	private String knownForTitles;

	@OneToMany(mappedBy = "name", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<TitlePrincipal> principals = new ArrayList<>();


	public boolean isAlive() {
		return deathYear == null;
	}

	public String[] getProfessions() {
		if (primaryProfession == null || primaryProfession.equals("\\N")) {
			return new String[0];
		}
		return primaryProfession.split(",");
	}

	public String[] getKnownForTitleIds() {
		if (knownForTitles == null || knownForTitles.equals("\\N")) {
			return new String[0];
		}
		return knownForTitles.split(",");
	}

}
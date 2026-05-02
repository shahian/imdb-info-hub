package shahian.movieinfo.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "title_basics", indexes = {
		@Index(name = "idx_title_tconst", columnList = "tconst"),
		@Index(name = "idx_title_type", columnList = "titleType"),
		@Index(name = "idx_title_start_year", columnList = "startYear"),
		@Index(name = "idx_title_genres", columnList = "genres")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Title {

	@Id
	@Column(name = "tconst", length = 20, nullable = false)
	private String tconst;

	@Column(name = "title_type", length = 50)
	private String titleType;

	@Column(name = "primary_title", length = 1000)
	private String primaryTitle;

	@Column(name = "original_title", length = 1000)
	private String originalTitle;

	@Column(name = "is_adult")
	private Boolean isAdult;

	@Column(name = "start_year")
	private Integer startYear;

	@Column(name = "end_year")
	private Integer endYear;

	@Column(name = "runtime_minutes")
	private Integer runtimeMinutes;

	@Column(name = "genres", columnDefinition = "TEXT")
	private String genres;

	@OneToOne(mappedBy = "title", cascade = CascadeType.ALL)
	@JsonIgnore
	private TitleCrew crew;

	@OneToOne(mappedBy = "title", cascade = CascadeType.ALL)
	@JsonIgnore
	private TitleRating rating;

	@OneToMany(mappedBy = "title", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	@JsonIgnore
	private List<TitlePrincipal> principals = new ArrayList<>();


}
package shahian.movieinfo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "title_crew")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitleCrew {

	@Id
	@Column(name = "tconst", length = 20, nullable = false)
	private String tconst;

	@Column(name = "directors",  columnDefinition = "TEXT")
	private String directors;

	@Column(name = "writers", columnDefinition = "TEXT")
	private String writers;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "tconst")
	@JsonIgnore
	private Title title;

}
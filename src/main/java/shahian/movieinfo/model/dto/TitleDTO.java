package shahian.movieinfo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TitleDTO {
	private String tconst;
	private String primaryTitle;
	private Integer startYear;
	private String genres;
	private Double averageRating;
	private Integer numVotes;
}
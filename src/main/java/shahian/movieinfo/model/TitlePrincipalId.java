package shahian.movieinfo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TitlePrincipalId implements Serializable {

	@Column(length = 20)
	private String tconst;

	private Integer ordering;
}
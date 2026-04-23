package shahian.movieinfo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "title_principals", indexes = {
		@Index(name = "idx_principal_title", columnList = "tconst"),
		@Index(name = "idx_principal_name", columnList = "nconst"),
		@Index(name = "idx_principal_category", columnList = "category"),
		@Index(name = "idx_principal_title_category", columnList = "tconst, category"),
		@Index(name = "idx_principal_name_category", columnList = "nconst, category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitlePrincipal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tconst", length = 20, nullable = false)
	private String tconst;

	@Column(name = "ordering")
	private Integer ordering;

	@Column(name = "nconst", length = 20, nullable = false)
	private String nconst;

	@Column(name = "category", length = 50)
	private String category;

	@Column(name = "job", length = 500)
	private String job;

	@Column(name = "characters", length = 1000)
	private String characters;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tconst", insertable = false, updatable = false)
	private Title title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nconst", insertable = false, updatable = false)
	private Name name;

	/**
	 * بررسی اینکه آیا شخص بازیگر است
	 */
	public boolean isActor() {
		return category != null && (category.equals("actor") || category.equals("actress"));
	}

	/**
	 * بررسی اینکه آیا شخص کارگردان است
	 */
	public boolean isDirector() {
		return category != null && category.equals("director");
	}

	/**
	 * بررسی اینکه آیا شخص نویسنده است
	 */
	public boolean isWriter() {
		return category != null && category.equals("writer");
	}

	/**
	 * گرفتن نقش‌های کاراکتری (characters)
	 */
	public String[] getCharacterArray() {
		if (characters == null || characters.equals("\\N")) {
			return new String[0];
		}
		return characters.split(",");
	}

}
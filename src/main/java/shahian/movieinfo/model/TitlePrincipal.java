package shahian.movieinfo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@EmbeddedId
	private TitlePrincipalId id;

	@Column(name = "nconst", length = 20, nullable = false)
	private String nconst;

	@Column(name = "category", length = 50)
	private String category;

	@Column(name = "job", length = 1000)
	private String job;

	@Column(name = "characters",  columnDefinition = "TEXT")
	private String characters;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tconst", insertable = false, updatable = false)
	@JsonIgnore
	private Title title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nconst", insertable = false, updatable = false)
	@JsonIgnore
	private Name name;

	public String getTconst() {
		return id != null ? id.getTconst() : null;
	}

	public void setTconst(String tconst) {
		if (id == null) {
			id = new TitlePrincipalId();
		}
		id.setTconst(tconst);
	}

	public Integer getOrdering() {
		return id != null ? id.getOrdering() : null;
	}

	public void setOrdering(Integer ordering) {
		if (id == null) {
			id = new TitlePrincipalId();
		}
		id.setOrdering(ordering);
	}
}
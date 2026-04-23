package shahian.movieinfo.model;

import jakarta.persistence.*;
import lombok.*;

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

	@Column(name = "directors", length = 1000)
	private String directors;

	@Column(name = "writers", length = 1000)
	private String writers;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "tconst")
	private Title title;


	public String[] getDirectorIds() {
		if (directors == null || directors.equals("\\N")) {
			return new String[0];
		}
		return directors.split(",");
	}


	public String[] getWriterIds() {
		if (writers == null || writers.equals("\\N")) {
			return new String[0];
		}
		return writers.split(",");
	}

	/**
	 * بررسی اینکه آیا یک شخص خاص هم کارگردان است هم نویسنده
	 */
	public boolean isBothDirectorAndWriter(String nconst) {
		boolean isDirector = false;
		boolean isWriter = false;

		for (String directorId : getDirectorIds()) {
			if (directorId.trim().equals(nconst)) {
				isDirector = true;
				break;
			}
		}

		for (String writerId : getWriterIds()) {
			if (writerId.trim().equals(nconst)) {
				isWriter = true;
				break;
			}
		}

		return isDirector && isWriter;
	}

	/**
	 * گرفتن لیست افرادی که هم کارگردان‌اند هم نویسنده
	 */
	public java.util.Set<String> getPeopleWhoAreBothDirectorAndWriter() {
		java.util.Set<String> directorSet = new java.util.HashSet<>();
		java.util.Set<String> writerSet = new java.util.HashSet<>();

		for (String id : getDirectorIds()) {
			directorSet.add(id.trim());
		}

		for (String id : getWriterIds()) {
			writerSet.add(id.trim());
		}

		directorSet.retainAll(writerSet);
		return directorSet;
	}

}
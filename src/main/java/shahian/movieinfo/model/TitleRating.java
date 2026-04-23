package shahian.movieinfo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "title_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitleRating {

	@Id
	@Column(name = "tconst", length = 20, nullable = false)
	private String tconst;

	@Column(name = "average_rating")
	private Double averageRating;

	@Column(name = "num_votes")
	private Integer numVotes;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "tconst")
	private Title title;

	/**
	 * بررسی اینکه آیا تعداد رای کافی برای رتبه‌بندی معتبر دارد
	 */
	public boolean hasEnoughVotes(int minVotes) {
		return numVotes != null && numVotes >= minVotes;
	}

	/**
	 * گرفتن امتیاز با دقت مشخص (مثلاً یک رقم اعشار)
	 */
	public double getRoundedRating() {
		if (averageRating == null) {
			return 0.0;
		}
		BigDecimal bd = BigDecimal.valueOf( averageRating);
		bd = bd.setScale(1, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * محاسبه امتیاز وزنی (برای رتبه‌بندی پیشرفته)
	 * @param globalAverageRating میانگین جهانی امتیازات
	 * @param minVotes حداقل رای مورد نیاز
	 */
	public double calculateWeightedScore(double globalAverageRating, int minVotes) {
		if (numVotes == null || numVotes < minVotes || averageRating == null) {
			return 0.0;
		}

		double v = numVotes;
		double m = minVotes;
		double R = averageRating;
		double C = globalAverageRating;

		return (v / (v + m)) * R + (m / (v + m)) * C;
	}

}
package shahian.movieinfo.service.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import shahian.movieinfo.exception.DataProcessingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataImportServiceImpl {

	private final JdbcTemplate jdbcTemplate;

	private final PlatformTransactionManager transactionManager;

	@Value("${imdb.data.name}")
	private Resource nameResource;

	@Value("${imdb.data.title}")
	private Resource titleResource;

	@Value("${imdb.data.crew}")
	private Resource crewResource;

	@Value("${imdb.data.ratings}")
	private Resource ratingsResource;

	@Value("${imdb.data.principals}")
	private Resource principalsResource;

	private static final int BATCH_SIZE = 200;

	private static final int MAX_RECORDS = 1_000_000;

	private static final boolean LIMIT_RECORDS = true;

	@PostConstruct
	public void init() {
		try {
			Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM name_basics", Long.class);
			if (count == null || count == 0) {
				log.info("No data found. Starting IMDb data import...");
				importAll();
			} else {
				log.info("Data already exists ({} names). Skipping import.", count);
			}
		} catch (Exception e) {
			log.error("FATAL: Import initialization failed", e);
			throw new RuntimeException("Import failed", e);
		}
	}

	public void importAll() {
		long start = System.currentTimeMillis();
		log.info("=== Starting full import at {} ===", new java.util.Date());

		try {
			jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
			jdbcTemplate.execute("SET WRITE_DELAY 0");
			//jdbcTemplate.execute("SET LOCK_MODE 0");

			clearTables();
			importNames();
			importTitles();
			importRatings();
			importCrews();
			importPrincipals();

			createIndices();

		} finally {
			try {
				jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
				jdbcTemplate.execute("SET WRITE_DELAY 100");
				jdbcTemplate.execute("CHECKPOINT");
			} catch (Exception e) {
				log.warn("Could not restore H2 settings: {}", e.getMessage());
			}
		}

		long end = System.currentTimeMillis();
		log.info("=== All imports finished in {} seconds ===", (end - start) / 1000);
	}

	private void clearTables() {
		try {
			jdbcTemplate.execute("TRUNCATE TABLE title_principals");
			jdbcTemplate.execute("TRUNCATE TABLE title_ratings");
			jdbcTemplate.execute("TRUNCATE TABLE title_crew");
			jdbcTemplate.execute("TRUNCATE TABLE title_basics");
			jdbcTemplate.execute("TRUNCATE TABLE name_basics");
			log.info("Tables cleared");
		} catch (Exception e) {
			log.debug("Could not truncate tables (first run?): {}", e.getMessage());
		}
	}

	private void createIndices() {
		log.info("Creating database indices...");
		String[] indices = {
				// Primary key indices
				"CREATE INDEX IF NOT EXISTS idx_name_basics_nconst ON name_basics(nconst)",
				"CREATE INDEX IF NOT EXISTS idx_title_basics_tconst ON title_basics(tconst)",
				"CREATE INDEX IF NOT EXISTS idx_title_crew_tconst ON title_crew(tconst)",
				"CREATE INDEX IF NOT EXISTS idx_title_ratings_tconst ON title_ratings(tconst)",
				"CREATE INDEX IF NOT EXISTS idx_title_principals_tconst ON title_principals(tconst)",
				"CREATE INDEX IF NOT EXISTS idx_title_principals_nconst ON title_principals(nconst)",

				"CREATE INDEX IF NOT EXISTS idx_title_basics_genres_start_year ON title_basics(genres, start_year)",

				"CREATE INDEX IF NOT EXISTS idx_title_principals_nconst_category ON title_principals(nconst, category)",

				"CREATE INDEX IF NOT EXISTS idx_title_ratings_votes_rating ON title_ratings(num_votes, average_rating)"
		};
		for (String idx : indices) {
			try {
				jdbcTemplate.execute(idx);
			} catch (Exception e) {
				log.debug("Index info: {}", e.getMessage());
			}
		}
		log.info("Indices created");
	}

	private int flushBatch(String sql, List<Object[]> batch) {
		if (batch.isEmpty()) {
			return 0;
		}
		TransactionTemplate tx = new TransactionTemplate(transactionManager);
		tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		tx.executeWithoutResult(status -> jdbcTemplate.batchUpdate(sql, batch));
		int flushed = batch.size();
		batch.clear();
		return flushed;
	}

	private void importCsv(Resource resource, String sql, CsvRowMapper mapper, String logPrefix) {
		List<Object[]> batch = new ArrayList<>(BATCH_SIZE);
		CSVFormat format = CSVFormat.TDF.builder()
				.setHeader()
				.setSkipHeaderRecord(true)
				.setQuote(null)
				.setNullString("\\N")
				.build();

		try (InputStream in = resource.getInputStream();
			 GZIPInputStream gzip = new GZIPInputStream(in);
			 InputStreamReader reader = new InputStreamReader(gzip);
			 CSVParser parser = format.parse(reader)) {

			int count = 0;
			long startTime = System.currentTimeMillis();

			for (CSVRecord record : parser) {
				if (LIMIT_RECORDS && count >= MAX_RECORDS) {
					log.info("{} reached limit of {} records, stopping import", logPrefix, MAX_RECORDS);
					break;
				}
				batch.add(mapper.map(record));
				if (batch.size() >= BATCH_SIZE) {
					count += flushBatch(sql, batch);
					if (count % 1_000_000 == 0) {
						long elapsed = (System.currentTimeMillis() - startTime) / 1000;
						double rate = count / (double) Math.max(1, elapsed);
						log.info("{} {} records imported ({} rec/sec)", logPrefix, count, String.format("%.0f", rate));
					}
				}
			}
			if (!batch.isEmpty()) {
				count += flushBatch(sql, batch);
			}
			log.info("{} import completed. Total: {} records", logPrefix, count);

		} catch (Exception e) {
			log.error("{} import failed", logPrefix, e);
			throw new DataProcessingException("Failed to import " + logPrefix + ": " + e.getMessage(), e);
		}
	}

	public void importNames() {
		importCsv(nameResource,
				"INSERT INTO name_basics (nconst, death_year) VALUES (?, ?)",
				r -> new Object[] { r.get("nconst"), parseYear(r.get("deathYear")) },
				"Names");
	}

	public void importTitles() {
		importCsv(titleResource,
				"INSERT INTO title_basics (tconst, start_year, genres) VALUES (?, ?, ?)",
				r -> new Object[] { r.get("tconst"), parseYear(r.get("startYear")), r.get("genres") },
				"Titles");
	}

	public void importCrews() {
		importCsv(crewResource,
				"INSERT INTO title_crew (tconst, directors, writers) VALUES (?, ?, ?)",
				r -> new Object[] { r.get("tconst"), r.get("directors"), r.get("writers") },
				"Crews");
	}

	public void importRatings() {
		importCsv(ratingsResource,
				"INSERT INTO title_ratings (tconst, average_rating, num_votes) VALUES (?, ?, ?)",
				r -> new Object[] { r.get("tconst"), Double.parseDouble(r.get("averageRating")), Integer.parseInt(r.get("numVotes")) },
				"Ratings");
	}

	public void importPrincipals() {
		importCsv(principalsResource,
				"INSERT INTO title_principals (tconst, nconst, category, ordering) VALUES (?, ?, ?, ?)",
				r -> new Object[] { r.get("tconst"), r.get("nconst"), r.get("category"), r.get("ordering") },
				"Principals");
	}

	private Integer parseYear(String year) {
		if (year == null || year.isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(year);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@FunctionalInterface
	private interface CsvRowMapper {
		Object[] map(CSVRecord record);
	}
}
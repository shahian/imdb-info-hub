package shahian.movieinfo.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shahian.movieinfo.model.*;
import shahian.movieinfo.repository.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataImportService {

	private final NameRepository nameRepository;
	private final TitleRepository titleRepository;
	private final TitleCrewRepository titleCrewRepository;
	private final TitleRatingRepository titleRatingRepository;
	private final TitlePrincipalRepository titlePrincipalRepository;
	private final ResourceLoader resourceLoader;

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

	private static final int BATCH_SIZE = 5000;

	@PostConstruct
	public void init() {
		if (nameRepository.count() == 0) {
			log.info("No data found. Starting IMDb data import from local files...");
			importAll();
		} else {
			log.info("Data already exists. Skipping import.");
		}
	}

	@Transactional
	public void importAll() {
		long start = System.currentTimeMillis();
		importNames();
		importTitles();
		importCrews();
		importRatings();
		importPrincipals();
		long end = System.currentTimeMillis();
		log.info("IMDb data import finished in {} ms", (end - start));
	}

	private void importNames() {
		log.info("Importing names from {}", nameResource.getFilename());
		List<Name> batch = new ArrayList<>();
		try (InputStream in = nameResource.getInputStream();
			 GZIPInputStream gzip = new GZIPInputStream(in);
			 InputStreamReader reader = new InputStreamReader(gzip);
			 CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader)) {

			for (CSVRecord record : parser) {
				Name name = Name.builder()
						.nconst(record.get("nconst"))
						.primaryName(record.get("primaryName"))
						.birthYear(parseYear(record.get("birthYear")))
						.deathYear(parseYear(record.get("deathYear")))
						.primaryProfession(nullIfNa(record.get("primaryProfession")))
						.knownForTitles(nullIfNa(record.get("knownForTitles")))
						.build();
				batch.add(name);
				if (batch.size() >= BATCH_SIZE) {
					nameRepository.saveAll(batch);
					batch.clear();
				}
			}
			if (!batch.isEmpty()) {
				nameRepository.saveAll(batch);
			}
			log.info("Names import completed.");
		} catch (Exception e) {
			log.error("Failed to import names", e);
		}
	}

	// سایر متدهای importTitles، importCrews، importRatings، importPrincipals
	// مشابه قبل ولی به جای URL از resource.getInputStream() استفاده کنید

	private void importTitles() {
		log.info("Importing titles from {}", titleResource.getFilename());
		List<Title> batch = new ArrayList<>();
		try (InputStream in = titleResource.getInputStream();
			 GZIPInputStream gzip = new GZIPInputStream(in);
			 InputStreamReader reader = new InputStreamReader(gzip);
			 CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader)) {

			for (CSVRecord record : parser) {
				Title title = Title.builder()
						.tconst(record.get("tconst"))
						.titleType(nullIfNa(record.get("titleType")))
						.primaryTitle(nullIfNa(record.get("primaryTitle")))
						.originalTitle(nullIfNa(record.get("originalTitle")))
						.isAdult("1".equals(record.get("isAdult")))
						.startYear(parseYear(record.get("startYear")))
						.endYear(parseYear(record.get("endYear")))
						.runtimeMinutes(parseInt(record.get("runtimeMinutes")))
						.genres(nullIfNa(record.get("genres")))
						.build();
				batch.add(title);
				if (batch.size() >= BATCH_SIZE) {
					titleRepository.saveAll(batch);
					batch.clear();
				}
			}
			if (!batch.isEmpty()) {
				titleRepository.saveAll(batch);
			}
			log.info("Titles import completed.");
		} catch (Exception e) {
			log.error("Failed to import titles", e);
		}
	}

	private void importCrews() {
		log.info("Importing crews from {}", crewResource.getFilename());
		List<TitleCrew> batch = new ArrayList<>();
		try (InputStream in = crewResource.getInputStream();
			 GZIPInputStream gzip = new GZIPInputStream(in);
			 InputStreamReader reader = new InputStreamReader(gzip);
			 CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader)) {

			for (CSVRecord record : parser) {
				TitleCrew crew = TitleCrew.builder()
						.tconst(record.get("tconst"))
						.directors(nullIfNa(record.get("directors")))
						.writers(nullIfNa(record.get("writers")))
						.build();
				batch.add(crew);
				if (batch.size() >= BATCH_SIZE) {
					titleCrewRepository.saveAll(batch);
					batch.clear();
				}
			}
			if (!batch.isEmpty()) {
				titleCrewRepository.saveAll(batch);
			}
			log.info("Crews import completed.");
		} catch (Exception e) {
			log.error("Failed to import crews", e);
		}
	}

	private void importRatings() {
		log.info("Importing ratings from {}", ratingsResource.getFilename());
		List<TitleRating> batch = new ArrayList<>();
		try (InputStream in = ratingsResource.getInputStream();
			 GZIPInputStream gzip = new GZIPInputStream(in);
			 InputStreamReader reader = new InputStreamReader(gzip);
			 CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader)) {

			for (CSVRecord record : parser) {
				TitleRating rating = TitleRating.builder()
						.tconst(record.get("tconst"))
						.averageRating(Double.valueOf(record.get("averageRating")))
						.numVotes(Integer.valueOf(record.get("numVotes")))
						.build();
				batch.add(rating);
				if (batch.size() >= BATCH_SIZE) {
					titleRatingRepository.saveAll(batch);
					batch.clear();
				}
			}
			if (!batch.isEmpty()) {
				titleRatingRepository.saveAll(batch);
			}
			log.info("Ratings import completed.");
		} catch (Exception e) {
			log.error("Failed to import ratings", e);
		}
	}

	private void importPrincipals() {
		log.info("Importing principals from {}", principalsResource.getFilename());
		List<TitlePrincipal> batch = new ArrayList<>();
		try (InputStream in = principalsResource.getInputStream();
			 GZIPInputStream gzip = new GZIPInputStream(in);
			 InputStreamReader reader = new InputStreamReader(gzip);
			 CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader)) {

			for (CSVRecord record : parser) {
				TitlePrincipal principal = TitlePrincipal.builder()
						.id(new TitlePrincipalId(record.get("tconst"), Integer.valueOf(record.get("ordering"))))
						.nconst(record.get("nconst"))
						.category(nullIfNa(record.get("category")))
						.job(nullIfNa(record.get("job")))
						.characters(nullIfNa(record.get("characters")))
						.build();
				batch.add(principal);
				if (batch.size() >= BATCH_SIZE) {
					titlePrincipalRepository.saveAll(batch);
					batch.clear();
				}
			}
			if (!batch.isEmpty()) {
				titlePrincipalRepository.saveAll(batch);
			}
			log.info("Principals import completed.");
		} catch (Exception e) {
			log.error("Failed to import principals", e);
		}
	}

	// Helper methods
	private Integer parseYear(String year) {
		if (year == null || year.isBlank() || "\\N".equals(year)) {
			return null;
		}
		try {
			return Integer.parseInt(year);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer parseInt(String value) {
		if (value == null || value.isBlank() || "\\N".equals(value)) {
			return null;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String nullIfNa(String value) {
		return (value == null || "\\N".equals(value)) ? null : value;
	}
}
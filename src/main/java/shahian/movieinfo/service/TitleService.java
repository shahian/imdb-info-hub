package shahian.movieinfo.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shahian.movieinfo.model.Title;
import shahian.movieinfo.repository.NameRepository;
import shahian.movieinfo.repository.TitleCrewRepository;
import shahian.movieinfo.repository.TitlePrincipalRepository;
import shahian.movieinfo.repository.TitleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TitleService {

	private final TitleCrewRepository titleCrewRepository;

	private final NameRepository nameRepository;

	private final TitlePrincipalRepository titlePrincipalRepository;

	private final TitleRepository titleRepository;


	public List<Title> getTitlesSameDirectorWriterAlive() {
		log.debug("Finding titles with same director and writer who is alive");

		Set<String> titleIds = new HashSet<>();
		var alivePersons = nameRepository.findByDeathYearIsNull();

		for (var person : alivePersons) {
			var crews = titleCrewRepository.findByBothDirectorAndWriter(person.getNconst());
			for (var crew : crews) {
				titleIds.add(crew.getTconst());
			}
		}

		if (titleIds.isEmpty()) {
			return Collections.emptyList();
		}
		return titleRepository.findAllById(titleIds);
	}

	public List<Title> getCommonTitlesForActors(String actor1Id, String actor2Id) {
		log.debug("Finding common titles for actors: {} and {}", actor1Id, actor2Id);

		List<String> commonIds = titlePrincipalRepository.findCommonMovieIds(actor1Id, actor2Id);
		if (commonIds.isEmpty()) {
			return Collections.emptyList();
		}

		return titleRepository.findAllById(commonIds);
	}
}
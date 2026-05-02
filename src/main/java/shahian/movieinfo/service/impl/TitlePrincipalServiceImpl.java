package shahian.movieinfo.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shahian.movieinfo.exception.ResourceNotFoundException;
import shahian.movieinfo.repository.TitlePrincipalRepository;
import shahian.movieinfo.service.TitlePrincipalService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TitlePrincipalServiceImpl implements TitlePrincipalService {
	private final TitlePrincipalRepository titlePrincipalRepository;

	@Override
	public List<String> getCommonMovieIds(String actor1Id, String actor2Id) {
		List<String> commonMovieIds = titlePrincipalRepository.findCommonMovieIds(actor1Id, actor2Id);
		if (commonMovieIds == null || commonMovieIds.isEmpty()) {
			log.info("No common titles found for actors {} and {}", actor1Id, actor2Id);
			throw new ResourceNotFoundException(
					String.format("No common movies found for actors: %s and %s", actor1Id, actor2Id)
			);
		}
		return commonMovieIds;
	}
}

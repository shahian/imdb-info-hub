package shahian.movieinfo.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shahian.movieinfo.model.Title;
import shahian.movieinfo.model.dto.TitleDTO;
import shahian.movieinfo.service.impl.TitleServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/titles")
@RequiredArgsConstructor
public class TitleController {

	private final TitleServiceImpl titleServiceImpl;


	@GetMapping("/same-director-writer-alive")
	@Operation(
			summary = "Get titles where director and writer are the same alive person",
			description = "Returns up to 200 titles. For full list, use API directly."
	)
	public ResponseEntity<List<TitleDTO>> getTitlesSameDirectorWriterAlive() {
		List<TitleDTO> titles = titleServiceImpl.getTitlesSameDirectorWriterAlive();
		List<TitleDTO> limitedTitles = titles.stream()
				.limit(200)
				.toList();
		return ResponseEntity.ok(limitedTitles);
	}

	@Operation(
			summary = " Get two actors and return all the titles in which both of them played at"
	)
	@GetMapping("/common-by-actors")
	public ResponseEntity<List<Title>> getCommonTitlesForActors(
			@RequestParam String actor1Id,
			@RequestParam String actor2Id) {
		List<Title> titles = titleServiceImpl.getCommonTitlesForActors(actor1Id, actor2Id);
		return ResponseEntity.ok(titles);
	}
}
package shahian.movieinfo.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import shahian.movieinfo.model.Title;
import shahian.movieinfo.service.TitleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/titles")
@RequiredArgsConstructor
public class TitleController {

	private final TitleService titleService;


	@GetMapping("/same-director-writer-alive")
	public ResponseEntity<List<Title>> getTitlesSameDirectorWriterAlive() {
		List<Title> titles = titleService.getTitlesSameDirectorWriterAlive();
		return ResponseEntity.ok(titles);
	}


	@GetMapping("/common-by-actors")
	public ResponseEntity<List<Title>> getCommonTitlesForActors(
			@RequestParam String actor1Id,
			@RequestParam String actor2Id) {
		List<Title> titles = titleService.getCommonTitlesForActors(actor1Id, actor2Id);
		return ResponseEntity.ok(titles);
	}
}
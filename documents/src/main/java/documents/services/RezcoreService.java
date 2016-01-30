package documents.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import documents.model.document.sections.DocumentSection;

@Service
public class RezcoreService {

	private final RestTemplate restTemplate;

	@Inject
	public RezcoreService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getRezcoreAnalysis(List<DocumentSection> documentSections) {
		StringBuilder sb = new StringBuilder();

		// sort by position
		documentSections.sort((s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition()));

		// create txt
		for (DocumentSection documentSection : documentSections) {
			sb.append(documentSection.asTxt());
			sb.append("\n");
		}

		// just to test
		return sb.toString();
	}
}

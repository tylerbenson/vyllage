package documents.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import documents.indeed.IndeedJobSearch;
import documents.indeed.IndeedResponse;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@Controller
public class IndeedController {

	private final IndeedJobSearch indeedJobSearch;
	private final DocumentService documentService;

	@Inject
	public IndeedController(IndeedJobSearch indeedJobSearch,
			DocumentService documentService) {
		this.indeedJobSearch = indeedJobSearch;
		this.documentService = documentService;
	}

	@RequestMapping(value = "/resume/{documentId}/jobs", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody IndeedResponse getJobs(HttpServletRequest request,
			@PathVariable Long documentId) throws ElementNotFoundException {

		List<String> queries = getQueries(documentId);

		long start = 0;
		return indeedJobSearch.search(queries, getIpAddress(request),
				request.getHeader("User-Agent"), start);
	}

	@RequestMapping(value = "/resume/{documentId}/jobs", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody IndeedResponse jobsPage(HttpServletRequest request,
			@PathVariable Long documentId, @RequestParam long start)
			throws ElementNotFoundException {

		List<String> queries = getQueries(documentId);

		return indeedJobSearch.search(queries, getIpAddress(request),
				request.getHeader("User-Agent"), start);
	}

	protected List<String> getQueries(Long documentId)
			throws ElementNotFoundException {
		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		List<String> degrees = documentSections.stream()
				.filter(ds -> ds instanceof EducationSection)
				.map(es -> ((EducationSection) es).getRole())
				.collect(Collectors.toList());

		// JobExperienceSection extends EducationSection so one is ok. I leave
		// this here since we will probably end up separating the classes one
		// day...
		// List<String> jobRoles = documentSections.stream()
		// .filter(ds -> ds instanceof JobExperienceSection)
		// .map(jes -> ((JobExperienceSection) jes).getRole())
		// .collect(Collectors.toList());

		List<String> queries = new ArrayList<>();
		queries.addAll(degrees);
		// queries.addAll(jobRoles);
		return queries;
	}

	protected String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null)
			ipAddress = request.getRemoteAddr();

		return ipAddress;
	}

	@RequestMapping(value = "/indeed/xml-example", method = RequestMethod.GET, produces = "application/xml")
	public @ResponseBody String XMLgetJobs() {
		return indeedJobSearch.searchXML();
	}

	@RequestMapping(value = "/indeed/html-example", method = RequestMethod.GET)
	public String HtmlGetJobs(Model model) {

		model.addAttribute("response", indeedJobSearch.search());
		return "indeed";
	}

}

package documents.files.html;

import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import documents.files.PhoneNumberFormatter;
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;

@Component
public class HTMLDocumentGenerator {

	private TemplateEngine templateEngine;

	@Inject
	public HTMLDocumentGenerator(final TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public String generateHTMLDocument(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String templateName) {
		Context ctx = new Context();

		format(resumeHeader);

		ctx.setVariable("header", resumeHeader);

		ctx.setVariable("sections", sections.stream().sorted(sortSections())
				.collect(Collectors.toList()));

		String htmlContent = templateEngine.process(templateName, ctx);
		return htmlContent;
	}

	/**
	 * Applies formatting to header phone number in Locale.US.
	 *
	 * @param resumeHeader
	 * @throws ParseException
	 */
	protected void format(DocumentHeader resumeHeader) {
		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		resumeHeader.setPhoneNumber(formatter.print(
				resumeHeader.getPhoneNumber(), Locale.US));
	}

	/**
	 * Sorts sections based on their sectionPosition value.
	 *
	 * @return sorted sections
	 */
	protected Comparator<? super DocumentSection> sortSections() {
		return (s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition());
	}
}

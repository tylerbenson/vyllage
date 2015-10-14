package documents.services.rules;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import com.newrelic.api.agent.NewRelic;

import documents.model.document.sections.DocumentSection;
import documents.utilities.FindDuplicates;

@Component
public class OrderSectionValidator {
	private final Logger logger = Logger.getLogger(OrderSectionValidator.class
			.getName());

	public void checkNullOrEmptyParameters(Long documentId,
			List<Long> documentSectionIds) {

		if (documentId == null || documentSectionIds == null
				|| documentSectionIds.isEmpty()) {
			IllegalArgumentException e = new IllegalArgumentException(
					"Expected at least one section id. Received none.");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}
	}

	public void checkDuplicateSectionIds(List<Long> documentSectionIds) {
		FindDuplicates finder = new FindDuplicates();
		if (!finder.findDuplicates(documentSectionIds).isEmpty()) {

			final String sectionIds = documentSectionIds.stream()
					.map(l -> l.toString()).collect(Collectors.joining(","));

			IllegalArgumentException e = new IllegalArgumentException(
					"Duplicate IDs found: [" + sectionIds + "]");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}
	}

	public void compareExistingIdsWithRequestedIds(
			List<Long> documentSectionIds,
			List<DocumentSection> documentSections) {

		final String documentSectionIdsToOrderAsString = documentSectionIds
				.stream().map(l -> l.toString())
				.collect(Collectors.joining(","));

		final String existingSectionIdsAsString = documentSections.stream()
				.map(ds -> ds.getSectionId().toString())
				.collect(Collectors.joining(","));

		if (documentSectionIds.size() != documentSections.size()) {

			IllegalArgumentException e = new IllegalArgumentException(
					"The amount of section ids does not match the number of existing sections in the database. "
							+ "Expected: "
							+ documentSections.size()
							+ " ids ["
							+ existingSectionIdsAsString
							+ "] received: "
							+ documentSectionIds.size()
							+ " ids ["
							+ documentSectionIdsToOrderAsString + "]");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		if (!documentSections.stream().map(ds -> ds.getSectionId())
				.collect(Collectors.toList()).containsAll(documentSectionIds)) {

			IllegalArgumentException e = new IllegalArgumentException(
					"The sections ids do not match the existing sections in the database. Expected: "
							+ " ids ["
							+ existingSectionIdsAsString
							+ "]"
							+ " received "
							+ " ids ["
							+ documentSectionIdsToOrderAsString + "]");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}
	}
}

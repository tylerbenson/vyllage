package documents.utilities;

import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;

import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import documents.model.OldDocumentSection;
import documents.model.constants.NewSectionType;
import documents.model.constants.SectionType;
import documents.model.document.sections.EducationSection;
import documents.model.document.sections.JobExperienceSection;
import documents.model.document.sections.SkillsSection;
import documents.model.document.sections.SummarySection;
import documents.repository.DocumentSectionRepository;

/**
 * This class migrates the old DocumentSection data to their new types.
 * Temporary, to be deleted after successful migration.
 * 
 * @author uh
 *
 */
@Component
public class DocumentSectionDataMigration {

	private final Logger logger = Logger
			.getLogger(DocumentSectionDataMigration.class.getName());

	@Autowired
	public DocumentSectionRepository repo;

	@Autowired
	public DSLContext sql;

	public DocumentSectionDataMigration() {
	}

	public void migrate() {
		logger.info("Starting migration.");
		for (Record record : sql.select(DOCUMENT_SECTIONS.fields())
				.from(DOCUMENT_SECTIONS).fetch()) {
			OldDocumentSection old = OldDocumentSection.fromJSON(record
					.getValue(DOCUMENT_SECTIONS.JSONDOCUMENT));
			// for the tests, since we are doing @DirtiesContext(classMode =
			// ClassMode.AFTER_CLASS) the previous values are still present for
			// the next tests causing the whole thing to fail

			if (old != null) {

				old.setDocumentId(record.getValue(DOCUMENT_SECTIONS.DOCUMENTID));
				old.setSectionVersion(record
						.getValue(DOCUMENT_SECTIONS.SECTIONVERSION));
				old.setLastModified(record.getValue(
						DOCUMENT_SECTIONS.LASTMODIFIED).toLocalDateTime());

				if (old.getType().equals(SectionType.ORGANIZATION)) {
					EducationSection newSection = new EducationSection();
					newSection.setCurrent(old.getIsCurrent());
					newSection.setDocumentId(old.getDocumentId());
					newSection.setEndDate(old.getEndDate());
					newSection.getHighlights().add(old.getHighlights());
					newSection.setLastModified(old.getLastModified());
					newSection.setLocation(old.getLocation());
					newSection.setNumberOfComments(old.getNumberOfComments());
					newSection.setOrganizationDescription(old
							.getOrganizationDescription());
					newSection.setOrganizationName(old.getOrganizationName());
					newSection.setRole(old.getRole());
					newSection.setRoleDescription(old.getRoleDescription());
					newSection.setSectionId(old.getSectionId());
					newSection.setSectionPosition(old.getSectionPosition());
					newSection.setSectionVersion(old.getSectionVersion());
					newSection.setStartDate(old.getStartDate());
					newSection.setState(old.getState());
					newSection.setTitle(old.getTitle());
					newSection.setType(NewSectionType.EDUCATION_SECTION.type());

					repo.save(newSection);
					logger.info("Saved Organization Section: " + newSection);
				}

				if (old.getType().equals(SectionType.EXPERIENCE)) {
					JobExperienceSection newSection = new JobExperienceSection();
					newSection.setCurrent(old.getIsCurrent());
					newSection.setDocumentId(old.getDocumentId());
					newSection.setEndDate(old.getEndDate());
					newSection.getHighlights().add(old.getHighlights());
					newSection.setLastModified(old.getLastModified());
					newSection.setLocation(old.getLocation());
					newSection.setNumberOfComments(old.getNumberOfComments());
					newSection.setOrganizationDescription(old
							.getOrganizationDescription());
					newSection.setOrganizationName(old.getOrganizationName());
					newSection.setRole(old.getRole());
					newSection.setRoleDescription(old.getRoleDescription());
					newSection.setSectionId(old.getSectionId());
					newSection.setSectionPosition(old.getSectionPosition());
					newSection.setSectionVersion(old.getSectionVersion());
					newSection.setStartDate(old.getStartDate());
					newSection.setState(old.getState());
					newSection.setTitle(old.getTitle());
					newSection.setType(NewSectionType.JOB_EXPERIENCE_SECTION
							.type());

					repo.save(newSection);
					logger.info("Saved Job Experience Section: " + newSection);
				}

				if (old.getType().equals(SectionType.FREEFORM)) {
					if (old.getTitle().equalsIgnoreCase("skills")) {
						SkillsSection newSection = new SkillsSection();

						// for the skills tag, the user will have to fix
						// these...
						if (old.getDescription() != null
								&& !old.getDescription().isEmpty()) {

							if (old.getDescription().contains(" "))
								for (String string : old.getDescription()
										.split(" "))
									newSection.getTags().add(string);
							else
								newSection.getTags().add(old.getDescription());
						}

						newSection.setDocumentId(old.getDocumentId());
						newSection.setLastModified(old.getLastModified());
						newSection.setNumberOfComments(old
								.getNumberOfComments());
						newSection.setSectionId(old.getSectionId());
						newSection.setSectionPosition(old.getSectionPosition());
						newSection.setSectionVersion(old.getSectionVersion());
						newSection.setState(old.getState());
						newSection.setTitle(old.getTitle());
						newSection
								.setType(NewSectionType.SKILLS_SECTION.type());

						repo.save(newSection);
						logger.info("Saved Skills Section: " + newSection);

					}

					if (old.getTitle().equalsIgnoreCase("career goal")) {
						SummarySection newSection = new SummarySection();
						newSection.setDescription(old.getDescription());
						newSection.setDocumentId(old.getDocumentId());
						newSection.setLastModified(old.getLastModified());
						newSection.setNumberOfComments(old
								.getNumberOfComments());
						newSection.setSectionId(old.getSectionId());
						newSection.setSectionPosition(old.getSectionPosition());
						newSection.setSectionVersion(old.getSectionVersion());
						newSection.setState(old.getState());
						newSection.setTitle("summary");
						newSection.setType(NewSectionType.SUMMARY_SECTION
								.type());

						repo.save(newSection);
						logger.info("Saved Summary Section: " + newSection);
					}
				}
			}
		}

		logger.info("Migration completed successfully.");
	}
}
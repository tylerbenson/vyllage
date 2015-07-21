package site;

import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;

import java.util.Arrays;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import documents.model.OldDocumentSection;
import documents.model.constants.NewSectionType;
import documents.model.constants.SectionType;
import documents.model.document.sections.JobExperienceSection;
import documents.model.document.sections.OrganizationSection;
import documents.model.document.sections.SkillsSection;
import documents.model.document.sections.SummarySection;
import documents.repository.DocumentSectionRepository;

@SpringBootApplication
@ComponentScan(basePackageClasses = { connections.Application.class,
		documents.Application.class, accounts.Application.class,
		Application.class })
@PropertySource("classpath:/connections/application.properties")
@PropertySource("classpath:/documents/application.properties")
@PropertySource("classpath:/accounts/application.properties")
public class Application implements CommandLineRunner {
	private static final Logger logger = Logger.getLogger(Application.class
			.getName());

	@Autowired
	private ApplicationContext context;

	@Autowired
	DocumentSectionRepository repo;

	@Autowired
	DSLContext sql;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		System.setProperty("spring.profiles.default", Profiles.DEV);
		if (Application.class.getResource("Application.class").getProtocol()
				.equals("file")) {
			if (System.getProperty("PROJECT_HOME") == null) {
				logger.info("PROJECT_HOME sys prop not set");
				System.exit(1);
			}
			application.setAdditionalProfiles(Profiles.DEV);
			logger.info("\n** Setting thymeleaf prefix to: "
					+ System.getProperty("PROJECT_HOME") + "/assets/public/\n");
			System.setProperty("spring.thymeleaf.prefix",
					"file:///" + System.getProperty("PROJECT_HOME")
							+ "/assets/public/");
		}
		String[] profiles = application.run(args).getEnvironment()
				.getActiveProfiles();
		logger.info("Using profiles: " + Arrays.toString(profiles));

	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Starting migration.");

		for (Record record : sql.select(DOCUMENT_SECTIONS.fields())
				.from(DOCUMENT_SECTIONS).fetch()) {
			OldDocumentSection old = OldDocumentSection.fromJSON(record
					.getValue(DOCUMENT_SECTIONS.JSONDOCUMENT));
			old.setDocumentId(record.getValue(DOCUMENT_SECTIONS.DOCUMENTID));
			old.setSectionVersion(record
					.getValue(DOCUMENT_SECTIONS.SECTIONVERSION));
			old.setLastModified(record.getValue(DOCUMENT_SECTIONS.LASTMODIFIED)
					.toLocalDateTime());

			if (old.getType().equals(SectionType.ORGANIZATION)) {
				OrganizationSection newSection = new OrganizationSection();
				newSection.setCurrent(old.getIsCurrent());
				newSection.setDescription(old.getDescription());
				newSection.setDocumentId(old.getDocumentId());
				newSection.setEndDate(old.getEndDate());
				newSection.setHighlights(old.getHighlights());
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
				newSection.setType(NewSectionType.ORGANIZATION_SECTION);

				repo.save(newSection);
				logger.info("Saved Organization Section: " + newSection);
			}

			if (old.getType().equals(SectionType.EXPERIENCE)) {
				JobExperienceSection newSection = new JobExperienceSection();
				newSection.setCurrent(old.getIsCurrent());
				newSection.setDescription(old.getDescription());
				newSection.setDocumentId(old.getDocumentId());
				newSection.setEndDate(old.getEndDate());
				newSection.setHighlights(old.getHighlights());
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
				newSection.setType(NewSectionType.JOB_EXPERIENCE_SECTION);

				repo.save(newSection);
				logger.info("Saved Job Experience Section: " + newSection);
			}

			if (old.getType().equals(SectionType.FREEFORM)) {
				if (old.getTitle().equalsIgnoreCase("skills")) {
					SkillsSection newSection = new SkillsSection();
					newSection.setDescription(old.getDescription());
					newSection.setDocumentId(old.getDocumentId());
					newSection.setLastModified(old.getLastModified());
					newSection.setNumberOfComments(old.getNumberOfComments());
					newSection.setSectionId(old.getSectionId());
					newSection.setSectionPosition(old.getSectionPosition());
					newSection.setSectionVersion(old.getSectionVersion());
					newSection.setState(old.getState());
					newSection.setTitle(old.getTitle());
					newSection.setType(NewSectionType.SKILLS_SECTION);

					repo.save(newSection);
					logger.info("Saved Skills Section: " + newSection);

				}

				if (old.getTitle().equalsIgnoreCase("career goal")) {
					SummarySection newSection = new SummarySection();
					newSection.setDescription(old.getDescription());
					newSection.setDocumentId(old.getDocumentId());
					newSection.setLastModified(old.getLastModified());
					newSection.setNumberOfComments(old.getNumberOfComments());
					newSection.setSectionId(old.getSectionId());
					newSection.setSectionPosition(old.getSectionPosition());
					newSection.setSectionVersion(old.getSectionVersion());
					newSection.setState(old.getState());
					newSection.setTitle(old.getTitle());
					newSection.setType(NewSectionType.SUMMARY_SECTION);

					repo.save(newSection);
					logger.info("Saved Summary Section: " + newSection);
				}
			}

		}

	}
}

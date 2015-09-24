package documents.model.document.sections;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import lombok.ToString;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.Assert;

import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.newrelic.api.agent.NewRelic;

import documents.model.constants.SectionType;
import documents.model.constants.Visibility;

@ToString
@JsonIgnoreProperties(value = { "documentId" })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
		@Type(value = AchievementsSection.class, name = "AchievementsSection"),
		@Type(value = CareerInterestsSection.class, name = "CareerInterestsSection"),
		@Type(value = JobExperienceSection.class, name = "JobExperienceSection"),
		@Type(value = EducationSection.class, name = "EducationSection"),
		@Type(value = PersonalReferencesSection.class, name = "PersonalReferencesSection"),
		@Type(value = ProfessionalReferencesSection.class, name = "ProfessionalReferencesSection"),
		@Type(value = ProjectsSection.class, name = "ProjectsSection"),
		@Type(value = SkillsSection.class, name = "SkillsSection"),
		@Type(value = SummarySection.class, name = "SummarySection"), })
public abstract class DocumentSection {
	private String title;
	private Long sectionId;
	private Long documentId;
	private Long sectionVersion;

	private String type;
	private Visibility state;
	private Long sectionPosition;
	private int numberOfComments;
	private int numberOfAdvices;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;

	public DocumentSection() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param type
	 *            any of the types in {@link SectionType}
	 */
	public String getType() {
		Assert.notNull(type);
		return type;
	}

	/**
	 * @param type
	 *            any of the types in {@link SectionType}
	 */
	public void setType(String type) {
		Assert.notNull(type);
		if (!SectionType.isValidType(type))
			throw new IllegalArgumentException(type
					+ " is not a valid section type.");
		this.type = type;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public Long getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(Long sectionPosition) {
		this.sectionPosition = sectionPosition;
	}

	public Visibility getState() {
		return state;
	}

	public void setState(Visibility state) {
		this.state = state;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getDocumentId() {
		return this.documentId;
	}

	public Long getSectionVersion() {
		return sectionVersion;
	}

	public void setSectionVersion(Long sectionVersion) {
		this.sectionVersion = sectionVersion;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public void setNumberOfAdvices(int integer) {
		numberOfAdvices = integer;
	}

	public int getNumberOfAdvices() {
		return numberOfAdvices;
	}

	public static DocumentSection fromJSON(String json) {

		final Logger logger = Logger.getLogger(DocumentSection.class.getName());

		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(json, DocumentSection.class);
		} catch (IOException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return null;
	}

	public String asJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

}

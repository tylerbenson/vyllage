package documents.model.document.sections;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import lombok.ToString;

import org.apache.commons.lang3.exception.ExceptionUtils;

import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.newrelic.api.agent.NewRelic;

import documents.model.constants.Visibility;
import documents.utilities.SectionTypeDeserializer;

@ToString
@JsonIgnoreProperties(value = { "documentId", "sectionVersion" })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public class DocumentSection {

	private String title;
	private Long sectionId;
	private Long documentId;
	private Long sectionVersion;

	@JsonDeserialize(using = SectionTypeDeserializer.class)
	private String type;
	private Visibility state;
	private Long sectionPosition;
	private int numberOfComments;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;

	private String description;

	public DocumentSection() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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

	public static DocumentSection fromJSON(String json) {

		final Logger logger = Logger.getLogger(DocumentSection.class.getName());

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerSubtypes(AchievementsSection.class);
		mapper.registerSubtypes(CareerInterestsSection.class);
		mapper.registerSubtypes(JobExperienceSection.class);
		mapper.registerSubtypes(OrganizationSection.class);
		mapper.registerSubtypes(PersonalReferencesSection.class);
		mapper.registerSubtypes(ProfessionalReferencesSection.class);
		mapper.registerSubtypes(ProjectSection.class);
		mapper.registerSubtypes(SkillsSection.class);
		mapper.registerSubtypes(SummarySection.class);
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
		mapper.registerSubtypes(AchievementsSection.class);
		mapper.registerSubtypes(CareerInterestsSection.class);
		mapper.registerSubtypes(JobExperienceSection.class);
		mapper.registerSubtypes(OrganizationSection.class);
		mapper.registerSubtypes(PersonalReferencesSection.class);
		mapper.registerSubtypes(ProfessionalReferencesSection.class);
		mapper.registerSubtypes(ProjectSection.class);
		mapper.registerSubtypes(SkillsSection.class);
		mapper.registerSubtypes(SummarySection.class);
		return mapper.writeValueAsString(this);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
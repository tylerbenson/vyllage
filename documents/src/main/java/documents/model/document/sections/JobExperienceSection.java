package documents.model.document.sections;

import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public class JobExperienceSection extends OrganizationSection {

}

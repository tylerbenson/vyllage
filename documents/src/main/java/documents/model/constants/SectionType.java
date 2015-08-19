package documents.model.constants;

import java.util.LinkedList;
import java.util.List;

/**
 * Enum with the valid section types. Provides static methods to get all the
 * values and validation.
 *
 * <br>
 * The String must correspond to any of the DocumentSection subclasses.
 *
 * @author uh
 *
 */
public enum SectionType {

	ACHIEVEMENTS_SECTION("AchievementsSection"), CAREER_INTERESTS_SECTION(
			"CareerInterestsSection"), EDUCATION_SECTION("EducationSection"), JOB_EXPERIENCE_SECTION(
			"JobExperienceSection"), PERSONAL_REFERENCES_SECTION(
			"PersonalReferencesSection"), PROFESSIONAL_REFERENCES_SECTION(
			"ProfessionalReferencesSection"), PROJECTS_SECTION(
			"ProjectsSection"), SKILLS_SECTION("SkillsSection"), SUMMARY_SECTION(
			"SummarySection");

	private final String type;

	SectionType(String type) {
		this.type = type;

	}

	public String type() {
		return type;
	}

	public static List<String> getTypes() {
		List<String> types = new LinkedList<>();
		for (SectionType newNewSectionType : SectionType.values())
			types.add(newNewSectionType.type());
		return types;
	}

	public static boolean isValidType(String type) {
		List<String> types = new LinkedList<>();
		for (SectionType newNewSectionType : SectionType.values())
			types.add(newNewSectionType.type());
		return types.contains(type);
	}
}

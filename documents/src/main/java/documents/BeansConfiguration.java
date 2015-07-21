package documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;

import togglz.controller.TogglzFeatureController;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import email.EmailBuilder;
import email.MailService;

@Configuration(value = "documents.BeansConfiguration")
public class BeansConfiguration {
	@Autowired
	private Environment environment;

	// @Bean(name = "documents.ObjectMapper")
	// public ObjectMapper objectMapper() {
	// ObjectMapper mapper = new ObjectMapper();
	// mapper.registerSubtypes(AchievementsSection.class);
	// mapper.registerSubtypes(CareerInterestsSection.class);
	// mapper.registerSubtypes(JobExperienceSection.class);
	// mapper.registerSubtypes(OrganizationSection.class);
	// mapper.registerSubtypes(PersonalReferencesSection.class);
	// mapper.registerSubtypes(ProfessionalReferencesSection.class);
	// mapper.registerSubtypes(ProjectSection.class);
	// mapper.registerSubtypes(SkillsSection.class);
	// mapper.registerSubtypes(SummarySection.class);
	// return mapper;
	// }

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}

	@Bean
	public Java8TimeDialect Java8TimeDialect() {
		return new Java8TimeDialect();
	}

	@Bean(name = "documents.emailBuilder")
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		EmailBuilder emailBuilder = new EmailBuilder(new MailService(
				templateEngine), environment);

		return emailBuilder;
	}

	@Bean
	public TogglzFeatureController togglzController() {
		return new TogglzFeatureController();
	}

}

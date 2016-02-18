package jobs.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import jobs.services.indeed.IndeedResult;

import org.jooq.tools.StringUtils;
import org.junit.Test;

public class JobOfferTest {

	@Test
	public void testOk() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertTrue(jobOffer.isValid());
	}

	@Test
	public void testCompanyNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany(null);
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testCompanyEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testDescriptionNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription(null);
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testDescriptionEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription(" ");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testJobExperienceNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(null);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testJobTypeNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(null);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testLocationNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation(null);
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testLocationEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testRoleNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole(null);

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testRoleEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testIndeedResultToJobOfferOk() {
		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate("Wed, 25 Nov 2015 08:29:28 GMT");
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");
		result.setFormattedLocationFull("Bangor, ME");

		JobOffer jobOffer = new JobOffer(result);

		assertFalse(StringUtils.isBlank(jobOffer.getCompany()));
		assertFalse(StringUtils.isBlank(jobOffer.getDescription()));
		assertFalse(StringUtils.isBlank(jobOffer.getLocation()));
		assertFalse(StringUtils.isBlank(jobOffer.getRole()));
		assertNotNull(jobOffer.getDateCreated());
		assertNotNull(jobOffer.getLastModified());
	}

	@Test
	public void testIndeedResultToJobOfferDateIsNullOnParseNotError() {
		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate(null);
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");

		JobOffer jobOffer = new JobOffer(result);

		assertNull(jobOffer.getDateCreated());
		assertNull(jobOffer.getLastModified());
	}

	@Test
	public void testIndeedResultToJobOfferDateIsWrongFormatOnParseNotError() {
		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate("Wed, 25 Nov 2015 GMT");
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");

		JobOffer jobOffer = new JobOffer(result);

		assertNull(jobOffer.getDateCreated());
		assertNull(jobOffer.getLastModified());
	}

	@Test
	public void testIndeedResultToJobOfferContainsJobType() {
		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate("Wed, 25 Nov 2015 08:29:28 GMT");
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a fulltime Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");
		result.setFormattedLocationFull("Bangor, ME");

		JobOffer jobOffer = new JobOffer(result);

		assertFalse(StringUtils.isBlank(jobOffer.getCompany()));
		assertFalse(StringUtils.isBlank(jobOffer.getDescription()));
		assertFalse(StringUtils.isBlank(jobOffer.getLocation()));
		assertFalse(StringUtils.isBlank(jobOffer.getRole()));
		assertNotNull(jobOffer.getDateCreated());
		assertNotNull(jobOffer.getLastModified());
		assertEquals(JobType.fulltime, jobOffer.getJobType());
	}

	@Test
	public void testIndeedResultToJobOfferContainsJobExperience() {
		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate("Wed, 25 Nov 2015 08:29:28 GMT");
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a fresh Graduate Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");
		result.setFormattedLocationFull("Bangor, ME");

		JobOffer jobOffer = new JobOffer(result);

		assertFalse(StringUtils.isBlank(jobOffer.getCompany()));
		assertFalse(StringUtils.isBlank(jobOffer.getDescription()));
		assertFalse(StringUtils.isBlank(jobOffer.getLocation()));
		assertFalse(StringUtils.isBlank(jobOffer.getRole()));
		assertNotNull(jobOffer.getDateCreated());
		assertNotNull(jobOffer.getLastModified());
		assertEquals(JobExperience.fresh_graduate, jobOffer.getJobExperience());
	}

	@Test
	public void testIndeedResultToJobOfferDoesNotContainJobExperienceOrJobType() {
		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate("Wed, 25 Nov 2015 08:29:28 GMT");
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");
		result.setFormattedLocationFull("Bangor, ME");

		JobOffer jobOffer = new JobOffer(result);

		assertFalse(StringUtils.isBlank(jobOffer.getCompany()));
		assertFalse(StringUtils.isBlank(jobOffer.getDescription()));
		assertFalse(StringUtils.isBlank(jobOffer.getLocation()));
		assertFalse(StringUtils.isBlank(jobOffer.getRole()));
		assertNotNull(jobOffer.getDateCreated());
		assertNotNull(jobOffer.getLastModified());
		assertNull(jobOffer.getJobExperience());
		assertNull(jobOffer.getJobType());

	}
}

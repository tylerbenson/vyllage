package site.controllers;

import org.junit.Assert;
import org.junit.Test;

public class StaticPagesControllerTest {

	@Test
	public void testContact() {
		StaticPagesController controller = new StaticPagesController();

		Assert.assertEquals("contact", controller.contact());

	}

	@Test
	public void testPrivacy() {
		StaticPagesController controller = new StaticPagesController();

		Assert.assertEquals("privacy", controller.privacy());
	}

	@Test
	public void testCareers() {
		StaticPagesController controller = new StaticPagesController();

		Assert.assertEquals("careers", controller.careers());
	}

}

package connections.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import connections.Application;
import connections.model.Contact;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ContactsRepositoryTest {

	@Autowired
	private IRepository<Contact> repository;

	@Test
	public void getContactTest() throws ElementNotFoundException {
		Contact contact = repository.get(1L);

		Assert.assertNotNull(contact);
		Assert.assertTrue(contact.getId().equals(1L));
	}

	@Test
	public void getAllContactsTest() {
		List<Contact> contacts = repository.getAll();

		Assert.assertNotNull(contacts);
		Assert.assertTrue(contacts.size() > 1);
	}

	@Test
	public void saveNewContactTest() {
		Contact contact = new Contact();
		contact.setUserName("test!");

		contact = repository.save(contact);

		Assert.assertNotNull("Contact was not saved. ", contact);
		Assert.assertNotNull("Contact was not saved. ", contact.getId());
		Assert.assertNotNull("Contact was not saved. ", contact.getUserName());
	}

	@Test
	public void updateContactTest() throws ElementNotFoundException {

		Contact contact = new Contact();
		contact.setUserName("test!");

		contact = repository.save(contact);
		Assert.assertNotNull("Contact was not saved. ", contact.getId());
		Assert.assertEquals("Contact was not saved. ", "test!",
				contact.getUserName());

		contact.setUserName("update!");

		contact = repository.save(contact);

		Assert.assertNotNull("Contact was not updated. ", contact);
		Assert.assertNotNull("Contact was not updated. ", contact.getId());
		Assert.assertEquals("Contact was not updated. ", "update!",
				contact.getUserName());

	}

	@Test(expected = ElementNotFoundException.class)
	public void deleteContactTest() throws ElementNotFoundException {
		repository.delete(4L);
		Contact contact = repository.get(4L);
		Assert.assertNull("Contact is not null.", contact);
	}

	@Test(expected = ElementNotFoundException.class)
	public void getNonExistantContact() throws ElementNotFoundException {
		Contact contact = repository.get(-1L);
		Assert.assertNull("Contact is not null.", contact);
	}
}

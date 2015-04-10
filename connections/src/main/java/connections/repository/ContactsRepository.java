package connections.repository;

import static connections.domain.tables.Contacts.CONTACTS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import connections.domain.tables.records.ContactsRecord;
import connections.model.Contact;

@Repository
public class ContactsRepository implements IRepository<Contact> {
	private final Logger logger = Logger.getLogger(ContactsRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public Contact get(Long contactId) throws ElementNotFoundException {
		ContactsRecord record = sql.fetchOne(CONTACTS,
				CONTACTS.ID.eq(contactId));
		if (record == null)
			throw new ElementNotFoundException("Contact with id '" + contactId
					+ "' could not be found.");

		Contact contact = recordToContact(record);
		return contact;
	}

	@Override
	public List<Contact> getAll() {
		Result<ContactsRecord> all = sql.fetch(CONTACTS);
		List<Contact> allContacts = new ArrayList<>();

		for (ContactsRecord contactsRecord : all)
			allContacts.add(recordToContact(contactsRecord));
		return allContacts;
	}

	@Override
	public Contact save(Contact contact) {

		ContactsRecord existingRecord = sql.fetchOne(CONTACTS,
				CONTACTS.ID.eq(contact.getUserId()));

		if (existingRecord == null) {
			ContactsRecord newRecord = sql.newRecord(CONTACTS);
			newRecord.setUsername(contact.getUserName());

			newRecord.store();
			contact.setUserId(newRecord.getId());

		} else {

			existingRecord.setUsername(contact.getUserName());
			existingRecord.update();
		}

		logger.info("Saved Contact: " + contact);

		return contact;
	}

	@Override
	public void delete(Long contactId) throws ElementNotFoundException {
		ContactsRecord existingRecord = sql.fetchOne(CONTACTS,
				CONTACTS.ID.eq(contactId));

		if (existingRecord == null)
			throw new ElementNotFoundException("Contact with id '" + contactId
					+ "' was not deleted, contact not found.");
		existingRecord.delete();
	}

	private Contact recordToContact(ContactsRecord contactsRecord) {
		Contact contact = new Contact();
		contact.setUserId(contactsRecord.getId());
		contact.setUserName(contactsRecord.getUsername());
		return contact;
	}
}

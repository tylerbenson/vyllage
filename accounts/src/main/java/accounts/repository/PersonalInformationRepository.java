package accounts.repository;

import static accounts.domain.tables.PersonalInformation.PERSONAL_INFORMATION;

import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.PersonalInformationRecord;
import accounts.model.account.PersonalInformation;

@Repository
public class PersonalInformationRepository {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(PersonalInformationRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public PersonalInformation get(Long userId) {
		PersonalInformationRecord record = sql.fetchOne(PERSONAL_INFORMATION,
				PERSONAL_INFORMATION.USER_ID.eq(userId));
		PersonalInformation pi = new PersonalInformation();
		pi.setUserId(record.getUserId());
		pi.setEmailUpdates(record.getEmailUpdates());
		pi.setGraduationDate(record.getGraduationDate().toLocalDateTime());
		pi.setPhoneNumber(record.getPhoneNumber());

		return pi;
	}

}

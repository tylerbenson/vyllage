package login.repository;

import static login.domain.tables.PersonalInformation.PERSONAL_INFORMATION;

import java.util.logging.Logger;

import login.domain.tables.records.PersonalInformationRecord;
import login.model.account.PersonalInformation;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PersonalInformationRepository {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(PersonalInformationRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public PersonalInformation get(Long userId) {
		PersonalInformationRecord record = sql.fetchOne(PERSONAL_INFORMATION,
				PERSONAL_INFORMATION.USERID.eq(userId));
		PersonalInformation pi = new PersonalInformation();
		pi.setUserId(record.getUserid());
		pi.setEmailUpdates(record.getEmailupdates());
		pi.setGraduationDate(record.getGraduationdate().toLocalDateTime());
		pi.setPhoneNumber(record.getPhonenumber());

		return pi;
	}

}

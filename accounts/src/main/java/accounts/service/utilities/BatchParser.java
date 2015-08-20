package accounts.service.utilities;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.Assert;

import accounts.validation.EmailValidator;

public class BatchParser {

	public List<ParsedAccount> parse(String csv) throws IOException {
		// format "email,firstName,middleName,lastName\n"

		if (csv == null || csv.isEmpty())
			throw new IllegalArgumentException("Cannot parse null arguments.");

		Reader in = new StringReader(csv.replace(';', ',').trim());

		return CSVFormat.DEFAULT
				.withHeader(BatchColumnsEnum.EMAIL.name(),
						BatchColumnsEnum.FIRST_NAME.name(),
						BatchColumnsEnum.MIDDLE_NAME.name(),
						BatchColumnsEnum.LAST_NAME.name()).parse(in)
				.getRecords().stream().map(ParsedAccount::new)
				.collect(Collectors.toList());
	}

	public class ParsedAccount {
		private String email;
		private String firstName;
		private String middleName;
		private String lastName;

		public ParsedAccount(CSVRecord record) {
			super();

			Assert.notNull(record.get(BatchColumnsEnum.EMAIL));
			Assert.isTrue(!record.get(BatchColumnsEnum.EMAIL).isEmpty());

			validateEmailAddress(record.get(BatchColumnsEnum.EMAIL).trim());

			this.email = record.get(BatchColumnsEnum.EMAIL).trim();

			this.firstName = record.get(BatchColumnsEnum.FIRST_NAME) != null
					&& !record.get(BatchColumnsEnum.FIRST_NAME).isEmpty() ? record
					.get(BatchColumnsEnum.FIRST_NAME).trim() : null;

			this.middleName = record.get(BatchColumnsEnum.MIDDLE_NAME) != null
					&& !record.get(BatchColumnsEnum.MIDDLE_NAME).isEmpty() ? record
					.get(BatchColumnsEnum.MIDDLE_NAME).trim() : null;

			this.lastName = record.get(BatchColumnsEnum.LAST_NAME) != null
					&& !record.get(BatchColumnsEnum.LAST_NAME).isEmpty() ? record
					.get(BatchColumnsEnum.LAST_NAME).trim() : null;
		}

		protected void validateEmailAddress(String email) {
			if (!EmailValidator.isValid(email))
				throw new IllegalArgumentException(
						"Contains invalid email address: " + email);
		}

		// lombok doesn't see this class

		@Override
		public String toString() {
			return "ParsedAccount [email=" + email + ", firstName=" + firstName
					+ ", middleName=" + middleName + ", lastName=" + lastName
					+ "]";
		}

		public String getEmail() {
			return email;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getMiddleName() {
			return middleName;
		}

		public String getLastName() {
			return lastName;
		}

	}

}

package editor.repository;

import static editor.domain.editor.tables.Documents.DOCUMENTS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRepository {

	private final Logger logger = Logger.getLogger(DocumentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	public void insertDocument(Long documentId) {
		sql.insertInto(DOCUMENTS, DOCUMENTS.ID, //
				DOCUMENTS.ACCOUNTID, //
				DOCUMENTS.DATECREATED, //
				DOCUMENTS.LASTMODIFIED, //
				DOCUMENTS.VISIBILITY) //
				.values(documentId, //
						0L, //
						Timestamp.valueOf(LocalDateTime.now()), //
						Timestamp.valueOf(LocalDateTime.now()), //
						true).execute();
	}

}

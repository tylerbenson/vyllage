package editor.repository;

import static editor.domain.editor.tables.Suggestions.SUGGESTIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.domain.editor.tables.records.SuggestionsRecord;
import editor.model.DocumentSection;
import editor.model.Suggestion;

@Repository
public class SuggestionRepository implements IRepository<Suggestion> {

	private final Logger logger = Logger.getLogger(SuggestionRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public Suggestion get(Long suggestionId) throws ElementNotFoundException {
		SuggestionsRecord record = sql.fetchOne(SUGGESTIONS,
				SUGGESTIONS.ID.eq(suggestionId));
		if (record == null)
			throw new ElementNotFoundException("Suggestion with id '"
					+ suggestionId + "' was not found.");

		Suggestion suggestion = recordToSuggestion(record);
		return suggestion;
	}

	@Override
	public List<Suggestion> getAll() {
		Result<SuggestionsRecord> all = sql.fetch(SUGGESTIONS);

		return all.stream().map(SuggestionRepository::recordToSuggestion)
				.collect(Collectors.toList());
	}

	@Override
	public Suggestion save(Suggestion suggestion) {

		if (suggestion.getId() == null)
			suggestion = createNew(suggestion);
		else
			suggestion = update(suggestion);

		logger.info("Saved suggestion: " + suggestion);

		return suggestion;
	}

	private Suggestion createNew(Suggestion suggestion) {
		SuggestionsRecord newRecord = sql.newRecord(SUGGESTIONS);

		newRecord.setSectionid(suggestion.getSectionId());
		newRecord.setSectionversion(suggestion.getSectionVersion());
		try {
			newRecord.setJsondocument(suggestion.getDocumentSection().asJSON());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		newRecord.setUsername(suggestion.getUserName());
		newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));

		newRecord.store();
		suggestion.setId(newRecord.getId());

		return suggestion;
	}

	private Suggestion update(Suggestion suggestion) {
		SuggestionsRecord existingRecord = sql.fetchOne(SUGGESTIONS,
				SUGGESTIONS.ID.eq(suggestion.getId()));

		if (existingRecord == null)
			return createNew(suggestion);

		existingRecord.setSectionid(suggestion.getSectionId());
		existingRecord.setSectionversion(suggestion.getSectionVersion());

		try {
			existingRecord.setJsondocument(suggestion.getDocumentSection()
					.asJSON());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		existingRecord.setUsername(suggestion.getUserName());
		existingRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));

		existingRecord.update();

		return suggestion;
	}

	@Override
	public void delete(Long suggestionId) {
		SuggestionsRecord record = sql.fetchOne(SUGGESTIONS,
				SUGGESTIONS.ID.eq(suggestionId));
		record.delete();
	}

	public static Suggestion recordToSuggestion(SuggestionsRecord record) {
		Suggestion suggestion = new Suggestion();
		suggestion.setId(record.getId());
		suggestion.setDocumentSection(DocumentSection.fromJSON(record
				.getJsondocument()));

		suggestion.setLastModified(record.getLastmodified().toLocalDateTime());
		suggestion.setSectionId(record.getSectionid());
		suggestion.setSectionVersion(record.getSectionversion());
		suggestion.setUserName(record.getUsername());

		return suggestion;
	}

}

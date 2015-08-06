package documents.repository;

import static documents.domain.tables.Suggestions.SUGGESTIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.NewRelic;

import documents.domain.tables.records.SuggestionsRecord;
import documents.model.Suggestion;
import documents.model.document.sections.DocumentSection;

@Repository
public class SuggestionRepository implements IRepository<Suggestion> {

	private final Logger logger = Logger.getLogger(SuggestionRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public Suggestion get(Long suggestionId) throws ElementNotFoundException {
		SuggestionsRecord record = sql.fetchOne(SUGGESTIONS,
				SUGGESTIONS.SUGGESTION_ID.eq(suggestionId));
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

		if (suggestion.getSuggestionId() == null)
			suggestion = createNew(suggestion);
		else
			suggestion = update(suggestion);

		logger.info("Saved suggestion: " + suggestion);

		return suggestion;
	}

	private Suggestion createNew(Suggestion suggestion) {
		SuggestionsRecord newRecord = sql.newRecord(SUGGESTIONS);

		newRecord.setSectionId(suggestion.getSectionId());
		newRecord.setSectionVersion(suggestion.getSectionVersion());
		try {
			newRecord.setJsonDocument(suggestion.getDocumentSection().asJSON());
		} catch (JsonProcessingException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		newRecord.setUserId(suggestion.getUserId());
		newRecord.setLastModified(Timestamp.valueOf(LocalDateTime.now(ZoneId
				.of("UTC"))));

		newRecord.store();
		suggestion.setSuggestionId(newRecord.getSuggestionId());

		return suggestion;
	}

	private Suggestion update(Suggestion suggestion) {
		SuggestionsRecord existingRecord = sql.fetchOne(SUGGESTIONS,
				SUGGESTIONS.SUGGESTION_ID.eq(suggestion.getSuggestionId()));

		if (existingRecord == null)
			return createNew(suggestion);

		existingRecord.setSectionId(suggestion.getSectionId());
		existingRecord.setSectionVersion(suggestion.getSectionVersion());

		try {
			existingRecord.setJsonDocument(suggestion.getDocumentSection()
					.asJSON());
		} catch (JsonProcessingException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		existingRecord.setUserId(suggestion.getUserId());
		existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));

		existingRecord.update();

		return suggestion;
	}

	@Override
	public void delete(Long suggestionId) {
		SuggestionsRecord record = sql.fetchOne(SUGGESTIONS,
				SUGGESTIONS.SUGGESTION_ID.eq(suggestionId));
		record.delete();
	}

	public static Suggestion recordToSuggestion(SuggestionsRecord record) {
		Suggestion suggestion = new Suggestion();
		suggestion.setSuggestionId(record.getSuggestionId());
		suggestion.setDocumentSection(DocumentSection.fromJSON(record
				.getJsonDocument()));

		suggestion.setLastModified(record.getLastModified().toLocalDateTime());
		suggestion.setSectionId(record.getSectionId());
		suggestion.setSectionVersion(record.getSectionVersion());
		suggestion.setUserId(record.getUserId());

		return suggestion;
	}
}

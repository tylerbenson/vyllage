package documents.repository;

import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;
import static documents.domain.tables.SectionAdvices.SECTION_ADVICES;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.NewRelic;

import documents.domain.tables.DocumentSections;
import documents.domain.tables.SectionAdvices;
import documents.domain.tables.records.SectionAdvicesRecord;
import documents.model.SectionAdvice;
import documents.model.constants.AdviceStatus;
import documents.model.document.sections.DocumentSection;

@Repository
public class SectionAdviceRepository implements IRepository<SectionAdvice> {

	private final Logger logger = Logger
			.getLogger(SectionAdviceRepository.class.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public SectionAdvice get(Long suggestionId) throws ElementNotFoundException {
		SectionAdvicesRecord record = sql.fetchOne(SECTION_ADVICES,
				SECTION_ADVICES.SECTION_ADVICE_ID.eq(suggestionId));
		if (record == null)
			throw new ElementNotFoundException("sectionAdvice with id '"
					+ suggestionId + "' was not found.");

		SectionAdvice sectionAdvice = recordToSectionAdvice(record);
		return sectionAdvice;
	}

	public List<SectionAdvice> getSectionAdvices(Long sectionId) {
		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");
		SectionAdvices ss = SECTION_ADVICES.as("ss");

		List<Record> records = sql
				.select(ss.fields())
				.from(s1)
				.leftOuterJoin(s2)
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION)))
				.join(ss).on(ss.SECTION_ID.eq(s1.ID))
				.where(s2.ID.isNull().and(ss.SECTION_ID.eq(sectionId))).fetch();

		List<SectionAdvice> sectionAdvices = new ArrayList<>();
		for (Record record : records) {

			SectionAdvice sectionAdvice = new SectionAdvice();
			sectionAdvice.setLastModified(record.getValue(
					SECTION_ADVICES.LAST_MODIFIED).toLocalDateTime());
			sectionAdvice.setSectionId(sectionId);
			sectionAdvice.setSectionVersion(record
					.getValue(SECTION_ADVICES.SECTION_VERSION));
			sectionAdvice.setSectionAdviceId(record
					.getValue(SECTION_ADVICES.SECTION_ADVICE_ID));
			sectionAdvice.setUserId(record.getValue(SECTION_ADVICES.USER_ID));
			sectionAdvice.setStatus(record.getValue(SECTION_ADVICES.STATUS));

			DocumentSection documentSection = DocumentSection.fromJSON(record
					.getValue(SECTION_ADVICES.JSON_DOCUMENT));

			sectionAdvice.setDocumentSection(documentSection);
			sectionAdvices.add(sectionAdvice);
		}

		return sectionAdvices;
	}

	@Override
	public List<SectionAdvice> getAll() {
		Result<SectionAdvicesRecord> all = sql.fetch(SECTION_ADVICES);

		return all.stream().map(SectionAdviceRepository::recordToSectionAdvice)
				.collect(Collectors.toList());
	}

	@Override
	public SectionAdvice save(SectionAdvice sectionAdvice) {

		if (sectionAdvice.getSectionAdviceId() == null)
			return createNew(sectionAdvice);
		else
			return update(sectionAdvice);
	}

	private SectionAdvice createNew(final SectionAdvice sectionAdvice) {
		SectionAdvicesRecord newRecord = sql.newRecord(SECTION_ADVICES);

		newRecord.setSectionId(sectionAdvice.getSectionId());
		newRecord.setSectionVersion(sectionAdvice.getSectionVersion());
		newRecord.setStatus(sectionAdvice.getStatus());
		try {
			newRecord.setJsonDocument(sectionAdvice.getDocumentSection()
					.asJSON());
		} catch (JsonProcessingException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		newRecord.setUserId(sectionAdvice.getUserId());
		newRecord.setLastModified(Timestamp.valueOf(LocalDateTime.now(ZoneId
				.of("UTC"))));

		newRecord.store();
		sectionAdvice.setSectionAdviceId(newRecord.getSectionAdviceId());
		sectionAdvice.setLastModified(newRecord.getLastModified()
				.toLocalDateTime());

		logger.fine("Saved section advice: " + sectionAdvice);
		return sectionAdvice;
	}

	private SectionAdvice update(final SectionAdvice sectionAdvice) {
		SectionAdvicesRecord existingRecord = sql.fetchOne(SECTION_ADVICES,
				SECTION_ADVICES.SECTION_ADVICE_ID.eq(sectionAdvice
						.getSectionAdviceId()));

		if (existingRecord == null)
			return createNew(sectionAdvice);

		existingRecord.setSectionId(sectionAdvice.getSectionId());
		existingRecord.setSectionVersion(sectionAdvice.getSectionVersion());
		existingRecord.setStatus(sectionAdvice.getStatus());

		try {
			existingRecord.setJsonDocument(sectionAdvice.getDocumentSection()
					.asJSON());
		} catch (JsonProcessingException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		existingRecord.setUserId(sectionAdvice.getUserId());
		existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));

		existingRecord.update();

		sectionAdvice.setLastModified(existingRecord.getLastModified()
				.toLocalDateTime());

		logger.fine("Updated section advice: " + sectionAdvice);
		return sectionAdvice;
	}

	@Override
	public void delete(Long sectionAdviceId) {
		SectionAdvicesRecord record = sql.fetchOne(SECTION_ADVICES,
				SECTION_ADVICES.SECTION_ADVICE_ID.eq(sectionAdviceId));
		record.delete();
	}

	public static SectionAdvice recordToSectionAdvice(
			SectionAdvicesRecord record) {
		SectionAdvice sectionAdvice = new SectionAdvice();
		sectionAdvice.setSectionAdviceId(record.getSectionAdviceId());
		sectionAdvice.setDocumentSection(DocumentSection.fromJSON(record
				.getJsonDocument()));

		sectionAdvice.setLastModified(record.getLastModified()
				.toLocalDateTime());
		sectionAdvice.setSectionId(record.getSectionId());
		sectionAdvice.setSectionVersion(record.getSectionVersion());
		sectionAdvice.setUserId(record.getUserId());
		sectionAdvice.setStatus(record.getStatus());

		return sectionAdvice;
	}

	public Map<Long, Integer> getNumberOfAdvicesForSections(
			List<Long> sectionIds) {
		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");
		SectionAdvices sa = SECTION_ADVICES.as("sa");

		Map<Long, Integer> sectionadvices = new HashMap<>();

		Result<Record2<Long, Integer>> fetch = sql
				.select(s1.ID, DSL.count(sa.SECTION_ADVICE_ID))
				.from(s1)
				.leftOuterJoin(s2)
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION)))
				.join(sa).on(sa.SECTION_ID.eq(s1.ID))
				.where(s2.ID.isNull().and(s1.ID.in(sectionIds)))
				.and(sa.STATUS.eq(AdviceStatus.pending.name())).groupBy(s1.ID)
				.fetch();

		for (Record2<Long, Integer> record : fetch) {
			sectionadvices.put((Long) record.getValue(0),
					(Integer) record.getValue(1));
		}

		return sectionadvices;
	}
}

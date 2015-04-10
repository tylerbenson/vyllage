package documents.repository;

import static documents.domain.tables.Comments.COMMENTS;
import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import documents.domain.tables.Comments;
import documents.domain.tables.DocumentSections;
import documents.domain.tables.records.CommentsRecord;
import documents.model.Comment;

@Repository
public class CommentRepository implements IRepository<Comment> {

	private final Logger logger = Logger.getLogger(CommentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Autowired
	private DocumentSectionRepository documentSectionRepository;

	@Override
	public Comment get(Long commentId) throws ElementNotFoundException {
		CommentsRecord record = sql.fetchOne(COMMENTS,
				COMMENTS.COMMENT_ID.eq(commentId));
		if (record == null)
			throw new ElementNotFoundException("Comment with id '" + commentId
					+ "' not found.");

		Comment comment = recordToComment(record);
		return comment;
	}

	@Override
	public List<Comment> getAll() {
		Result<CommentsRecord> all = sql.fetch(COMMENTS);

		return all.stream().map(CommentRepository::recordToComment)
				.collect(Collectors.toList());
	}

	@Override
	public Comment save(Comment comment) {
		CommentsRecord existingRecord = null;
		if (comment.getCommentId() != null)
			existingRecord = sql.fetchOne(COMMENTS,
					COMMENTS.COMMENT_ID.eq(comment.getCommentId()));

		if (existingRecord == null) {
			CommentsRecord newRecord = sql.newRecord(COMMENTS);

			newRecord.setOtherCommentId(comment.getOtherCommentId());
			newRecord.setCommentText(comment.getCommentText());
			newRecord.setSectionId(comment.getSectionId());
			newRecord.setSectionVersion(comment.getSectionVersion());
			newRecord.setUserId(comment.getUserId());
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			newRecord.store();
			comment.setCommentId(newRecord.getCommentId());

		} else {

			existingRecord.setOtherCommentId(comment.getOtherCommentId());
			existingRecord.setCommentText(comment.getCommentText());
			existingRecord.setSectionId(comment.getSectionId());
			existingRecord.setSectionVersion(comment.getSectionVersion());
			existingRecord.setUserId(comment.getUserId());
			existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			existingRecord.update();
		}

		logger.info("Saved comment: " + comment);

		return comment;
	}

	@Override
	public void delete(Long commentId) {
		CommentsRecord existingRecord = sql.fetchOne(COMMENTS,
				COMMENTS.COMMENT_ID.eq(commentId));
		existingRecord.delete();

	}

	public static Comment recordToComment(CommentsRecord record) {
		Comment comment = new Comment();
		comment.setCommentId(record.getCommentId());
		comment.setOtherCommentId(record.getOtherCommentId());
		comment.setUserId(record.getUserId());
		comment.setSectionId(record.getSectionId());
		comment.setSectionVersion(record.getSectionVersion());
		comment.setLastModified(record.getLastModified().toLocalDateTime());
		comment.setCommentText(record.getCommentText());
		return comment;
	}

	public List<Comment> getCommentsForSection(Long sectionId) {

		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");
		Comments c = COMMENTS.as("c");

		List<Record> records = sql
				.select(c.fields())
				.from(s1)
				.leftOuterJoin(s2)
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION))).join(c)
				.on(c.SECTION_ID.eq(s1.ID))
				.where(s2.ID.isNull().and(c.SECTION_ID.eq(sectionId))).fetch();

		List<Comment> comments = new ArrayList<>();
		for (Record record : records) {
			Comment comment = new Comment();
			comment.setCommentId(record.getValue(COMMENTS.COMMENT_ID));
			comment.setCommentText(record.getValue(COMMENTS.COMMENT_TEXT));
			comment.setLastModified(record.getValue(COMMENTS.LAST_MODIFIED)
					.toLocalDateTime());
			comment.setOtherCommentId(record
					.getValue(COMMENTS.OTHER_COMMENT_ID));
			comment.setSectionId(sectionId);
			comment.setSectionVersion(record.getValue(COMMENTS.SECTION_VERSION));
			comment.setUserId(record.getValue(COMMENTS.USER_ID));
			comments.add(comment);
		}

		return comments;

	}

	public Map<Long, Integer> getNumberOfCommentsForSections(
			List<Long> sectionIds) {
		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");
		Comments c = COMMENTS.as("c");

		Map<Long, Integer> sectionComments = new HashMap<>();

		Result<Record2<Long, Integer>> fetch = sql
				.select(s1.ID, DSL.count(c.COMMENT_ID))
				.from(s1)
				.leftOuterJoin(s2)
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION))).join(c)
				.on(c.SECTION_ID.eq(s1.ID))
				.where(s2.ID.isNull().and(s1.ID.in(sectionIds))).groupBy(s1.ID)
				.fetch();

		for (Record2<Long, Integer> record : fetch) {
			sectionComments.put((Long) record.getValue(0),
					(Integer) record.getValue(1));
		}

		return sectionComments;
	}

}

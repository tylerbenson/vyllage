package documents.repository;

import static documents.domain.tables.Comments.COMMENTS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import documents.domain.tables.records.CommentsRecord;
import documents.model.Comment;

@Repository
public class CommentRepository implements IRepository<Comment> {

	private final Logger logger = Logger.getLogger(CommentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

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
		CommentsRecord existingRecord = sql.fetchOne(COMMENTS,
				COMMENTS.COMMENT_ID.eq(comment.getCommentId()));

		if (existingRecord == null) {
			CommentsRecord newRecord = sql.newRecord(COMMENTS);

			newRecord.setOtherCommentId(comment.getOtherCommentId());
			newRecord.setCommentText(comment.getCommentText());
			newRecord.setSectionId(comment.getSectionId());
			newRecord.setSectionVersion(comment.getSectionVersion());
			newRecord.setUserId(comment.getUserId());
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime.now()));

			newRecord.store();
			comment.setCommentId(newRecord.getCommentId());

		} else {

			existingRecord.setOtherCommentId(comment.getOtherCommentId());
			existingRecord.setCommentText(comment.getCommentText());
			existingRecord.setSectionId(comment.getSectionId());
			existingRecord.setSectionVersion(comment.getSectionVersion());
			existingRecord.setUserId(comment.getUserId());
			existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now()));

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

}

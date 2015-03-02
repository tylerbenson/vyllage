package editor.repository;

import static editor.domain.tables.Comments.COMMENTS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import editor.domain.tables.records.CommentsRecord;
import editor.model.Comment;

@Repository
public class CommentRepository implements IRepository<Comment> {

	private final Logger logger = Logger.getLogger(CommentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public Comment get(Long commentId) throws ElementNotFoundException {
		CommentsRecord record = sql.fetchOne(COMMENTS,
				COMMENTS.ID.eq(commentId));
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
				COMMENTS.ID.eq(comment.getId()));

		if (existingRecord == null) {
			CommentsRecord newRecord = sql.newRecord(COMMENTS);

			newRecord.setCommentid(comment.getCommentId());
			newRecord.setCommenttext(comment.getCommentText());
			newRecord.setSectionid(comment.getSectionId());
			newRecord.setSectionversion(comment.getSectionVersion());
			newRecord.setUsername(comment.getUserName());
			newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));

			newRecord.store();
			comment.setId(newRecord.getId());

		} else {

			existingRecord.setCommentid(comment.getCommentId());
			existingRecord.setCommenttext(comment.getCommentText());
			existingRecord.setSectionid(comment.getSectionId());
			existingRecord.setSectionversion(comment.getSectionVersion());
			existingRecord.setUsername(comment.getUserName());
			existingRecord.setLastmodified(Timestamp.valueOf(LocalDateTime
					.now()));

			existingRecord.update();
		}

		logger.info("Saved comment: " + comment);

		return comment;
	}

	@Override
	public void delete(Long commentId) {
		CommentsRecord existingRecord = sql.fetchOne(COMMENTS,
				COMMENTS.ID.eq(commentId));
		existingRecord.delete();

	}

	public static Comment recordToComment(CommentsRecord record) {
		Comment comment = new Comment();
		comment.setId(record.getId());
		comment.setCommentId(record.getCommentid());
		comment.setUserName(record.getUsername());
		comment.setSectionId(record.getSectionid());
		comment.setSectionVersion(record.getSectionversion());
		comment.setLastModified(record.getLastmodified().toLocalDateTime());
		comment.setCommentText(record.getCommenttext());
		return comment;
	}

}

package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;
import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@ToString
@JsonIgnoreProperties(value = { "deleted" })
public class Comment {
	private Long commentId;
	private Long otherCommentId;
	private Long sectionId;
	private Long sectionVersion;
	private Long userId;
	private String commentText;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;

	// not saved in the DB
	private String userName;

	// not saved in the DB
	private String avatarUrl;

	// not saved in the DB
	private boolean canDeleteComment;

	// to check if we need to load the user names.
	private boolean deleted;

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long id) {
		this.commentId = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getCommentText() {
		return commentText;
	}

	/**
	 * Sets the comment text, if the commemt was deleted it marks it as not
	 * deleted.
	 * 
	 * @param commentText
	 */
	public void setCommentText(String commentText) {
		this.deleted = false;
		this.commentText = commentText;
	}

	public Long getSectionVersion() {
		return sectionVersion;
	}

	public void setSectionVersion(Long sectionVersion) {
		this.sectionVersion = sectionVersion;
	}

	/**
	 * @return the id of the other comment this one refers to.
	 */
	public Long getOtherCommentId() {
		return otherCommentId;
	}

	/**
	 * @param commentId
	 *            the Id of the comment this one will refer to.
	 */
	public void setOtherCommentId(Long commentId) {
		this.otherCommentId = commentId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public boolean getCanDeleteComment() {
		return canDeleteComment;
	}

	public void setCanDeleteComment(boolean canDeleteComment) {
		this.canDeleteComment = canDeleteComment;
	}

	/**
	 * Marks a comment as deleted and changes it's text to '[deleted]'
	 */
	public void markDeleted() {
		this.commentText = "[deleted]";
		this.deleted = true;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}

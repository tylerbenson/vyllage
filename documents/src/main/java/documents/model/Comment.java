package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class Comment {
	private Long commentId;
	private Long otherCommentId;
	private Long sectionId;
	private Long sectionVersion;
	private Long userId;
	private String commentText;
	private LocalDateTime lastModified;

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

	public void setCommentText(String commentText) {
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

}

package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class Comment {
	private Long id;
	private Long commentId;
	private Long sectionId;
	private Long sectionVersion;
	private String userName;
	private String commentText;
	private LocalDateTime lastModified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
	public Long getCommentId() {
		return commentId;
	}

	/**
	 * @param commentId
	 *            the Id of the comment this one will refer to.
	 */
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

}

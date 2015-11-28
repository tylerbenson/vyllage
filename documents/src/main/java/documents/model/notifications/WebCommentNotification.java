package documents.model.notifications;

import lombok.ToString;

@ToString
public class WebCommentNotification extends AbstractWebNotification {

	private Long commentId;

	private String sectionTitle;

	public WebCommentNotification() {
	}

	public WebCommentNotification(CommentNotification commentNotification) {
		super(commentNotification.getUserId(), commentNotification
				.getDateCreated(), commentNotification.getCommentUserId());

		this.commentId = commentNotification.getCommentId();
		this.sectionTitle = commentNotification.getSectionTitle();
		this.setType("WebCommentNotification");
	}

	public Long getCommentId() {
		return commentId;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

}

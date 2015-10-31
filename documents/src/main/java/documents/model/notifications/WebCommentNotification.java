package documents.model.notifications;

public class WebCommentNotification extends AbstractWebNotification {

	private Long commentUserId;

	private Long commentId;

	private String sectionTitle;

	public WebCommentNotification() {
	}

	public WebCommentNotification(CommentNotification commentNotification) {
		super(commentNotification.getUserId(), commentNotification
				.getDateCreated());

		this.commentUserId = commentNotification.getCommentUserId();
		this.commentId = commentNotification.getCommentId();
		this.sectionTitle = commentNotification.getSectionTitle();
	}

	public Long getCommentUserId() {
		return commentUserId;
	}

	public Long getCommentId() {
		return commentId;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

}

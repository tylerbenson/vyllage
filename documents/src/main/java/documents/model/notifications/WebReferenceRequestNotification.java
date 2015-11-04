package documents.model.notifications;

public class WebReferenceRequestNotification extends AbstractWebNotification {

	public WebReferenceRequestNotification() {
	}

	public WebReferenceRequestNotification(ReferenceRequestNotification rrn) {
		super(rrn.getUserId(), rrn.getDateCreated(), rrn
				.getReferenceRequestUserId());
		this.setType("WebReferenceRequestNotification");
	}

}

package documents.model.notifications;

import lombok.ToString;

@ToString
public class WebReferenceRequestNotification extends AbstractWebNotification {

	public WebReferenceRequestNotification() {
	}

	public WebReferenceRequestNotification(ReferenceRequestNotification rrn) {
		super(rrn.getUserId(), rrn.getDateCreated(), rrn
				.getReferenceRequestUserId());
		this.setType("WebReferenceRequestNotification");
	}

}

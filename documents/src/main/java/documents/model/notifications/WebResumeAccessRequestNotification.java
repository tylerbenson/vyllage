package documents.model.notifications;

import lombok.ToString;

@ToString
public class WebResumeAccessRequestNotification extends AbstractWebNotification {

	public WebResumeAccessRequestNotification() {
	}

	public WebResumeAccessRequestNotification(
			ResumeAccessRequestNotification rarn) {
		super(rarn.getUserId(), rarn.getDateCreated(), rarn
				.getResumeRequestUserId());
		this.setType("WebResumeAccessRequestNotification");
	}
}

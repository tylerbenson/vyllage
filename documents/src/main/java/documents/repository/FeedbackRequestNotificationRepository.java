package documents.repository;

import java.util.List;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import documents.model.notifications.FeedbackRequestNotification;

@Repository
public class FeedbackRequestNotificationRepository {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(FeedbackRequestNotificationRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public List<FeedbackRequestNotification> get(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(FeedbackRequestNotification feedbackRequestNotification) {
		// TODO Auto-generated method stub

	}

}

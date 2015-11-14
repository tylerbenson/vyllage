package documents.controller;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import user.common.User;
import documents.model.Comment;
import documents.model.Document;
import documents.repository.DocumentAccessRepository;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.notification.NotificationService;

@RunWith(MockitoJUnitRunner.class)
public class CommentControllerTest {

	private CommentController commentController;

	@Mock
	private DocumentService documentService;

	@Mock
	private AccountService accountService;

	@Mock
	private NotificationService notificationService;

	@Mock
	private DocumentAccessRepository documentAccessRepository;

	@Before
	public void setUp() {
		commentController = new CommentController(documentService,
				accountService, notificationService, documentAccessRepository);
	}

	@Test
	public void testCanDeleteAllComments() {

		Long commentId = 1L;
		Long userId = 1L;
		Long otherUserId = 52L;

		Comment comment = comments(5L).get(0);
		comment.setCommentId(commentId);

		comment.setUserId(otherUserId);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		Document document = new Document();
		document.setUserId(userId);

		assertTrue(commentController.canDeleteComment(commentId, comment, user,
				document));
	}

	@Test
	public void testCanDeleteOwnComment() {
		Long commentId = 1L;
		Long userId = 1L;
		Long otherUserId = 52L;

		Comment comment = comments(5L).get(0);
		comment.setCommentId(commentId);
		comment.setUserId(userId);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		Document document = new Document();
		document.setUserId(otherUserId);

		assertTrue(commentController.canDeleteComment(commentId, comment, user,
				document));
	}

	private List<Comment> comments(Long sectionId) {
		Comment comment = new Comment();
		comment.setUserId(0L);
		comment.setSectionId(sectionId);
		comment.setCommentText("test");

		return Arrays.asList(comment);
	}
}

package documents.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.Application;
import documents.model.Comment;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CommentRepositoryTest {

	@Autowired
	private IRepository<Comment> repository;

	@Test
	public void testRetrieveExistingDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the comment inserted in V2__init.sql...
		Comment comment = repository.get(0L);

		Assert.assertNotNull("Comment is null.", comment);
		Assert.assertTrue(comment.getCommentId().equals(0L));
	}

	@Test
	public void commentSaveTest() {

		Comment comment1 = generateComment();
		Comment comment2 = generateComment();

		comment1 = repository.save(comment1);
		comment2 = repository.save(comment2);

		Assert.assertNotNull("Comment1 is null.", comment1);
		Assert.assertNotNull("Comment2 is null.", comment2);
		Assert.assertNotNull("Expected id ", comment1.getCommentId());
		Assert.assertNotNull("Expected id ", comment2.getCommentId());
	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteComment() throws ElementNotFoundException {
		Comment comment = generateComment();

		comment = repository.save(comment);
		Long id = comment.getCommentId();

		repository.delete(comment.getCommentId());

		comment = repository.get(id);

		Assert.assertNull("Comment is not null.", comment);
	}

	private Comment generateComment() {
		Comment comment = new Comment();
		comment.setCommentText("Testing!");
		comment.setSectionId(130L);
		comment.setSectionVersion(1L);
		comment.setUserId(0L);
		return comment;
	}

}

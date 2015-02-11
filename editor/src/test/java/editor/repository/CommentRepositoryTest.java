package editor.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import editor.Application;
import editor.model.Comment;

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
		Assert.assertTrue(comment.getId().equals(0L));
	}

	@Test
	public void commentSaveTest() {

		Comment comment1 = generateComment();
		Comment comment2 = generateComment();

		comment1 = repository.save(comment1);
		comment2 = repository.save(comment2);

		Assert.assertNotNull("Comment1 is null.", comment1);
		Assert.assertNotNull("Comment2 is null.", comment2);
		Assert.assertTrue("Expected id 2 got " + comment1.getId(), comment1
				.getId().equals(2L));
		Assert.assertTrue("Expected id 3 got " + comment2.getId(), comment2
				.getId().equals(3L));
	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the comment inserted in V2__init.sql...
		Comment comment = generateComment();

		comment = repository.save(comment);
		Long id = comment.getId();

		repository.delete(comment.getId());

		comment = repository.get(id);

		Assert.assertNull("Comment is not null.", comment);
	}

	private Comment generateComment() {
		Comment comment = new Comment();
		comment.setCommentText("Testing!");
		comment.setSectionId(124L);
		comment.setSectionVersion(1L);
		comment.setUserName("link");
		return comment;
	}

}

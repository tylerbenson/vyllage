package jobs.services.rezscore;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import jobs.ApplicationTestConfig;
import jobs.services.DocumentService;
import jobs.services.rezscore.result.Advice;
import jobs.services.rezscore.result.Extended;
import jobs.services.rezscore.result.File;
import jobs.services.rezscore.result.Industry;
import jobs.services.rezscore.result.Language;
import jobs.services.rezscore.result.Rezscore;
import jobs.services.rezscore.result.RezscoreResult;
import jobs.services.rezscore.result.Score;
import jobs.services.rezscore.result.Text;
import jobs.services.rezscore.result.Tip;
import jobs.services.rezscore.result.Word;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RezscoreServiceTest {

	private RezscoreService rezscoreService;

	// replace with new RestTemplate() to test real endpoint
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private RedisCache<String, RezscoreResult> redisCache;

	@Inject
	private DocumentService documentService;

	@Before
	public void setUp() {
		rezscoreService = new RezscoreService(restTemplate, redisCache,
				documentService);

		// comment this to test real endpoint

		@SuppressWarnings("unchecked")
		ResponseEntity<Rezscore> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
						Mockito.eq(Rezscore.class))).thenReturn(response);
		Rezscore rezscore = new Rezscore();
		Advice advice = new Advice();
		advice.setTip(new Tip());

		Language language = new Language();
		language.setWord(new Word());

		File file = new File();

		rezscore.setAdvice(advice);
		rezscore.setExtended(new Extended());

		rezscore.setFile(file);
		rezscore.setIndustry(new Industry());
		rezscore.setLanguage(language);
		rezscore.setScore(new Score());

		rezscore.setText(new Text());

		when(response.getBody()).thenReturn(rezscore);

	}

	@Test
	public void test() {

	}

}

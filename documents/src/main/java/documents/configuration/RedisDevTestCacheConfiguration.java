package documents.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.embedded.RedisServer;
import util.profiles.Profiles;
import documents.services.rezscore.RedisCache;
import documents.services.rezscore.result.RezscoreResult;

@Configuration(value = "documents.RedisDevTestCacheConfiguration")
@Profile({ Profiles.DEV, Profiles.TEST })
public class RedisDevTestCacheConfiguration {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setPort(6377);
		jedisConnectionFactory.setUsePool(true);
		return jedisConnectionFactory;
	}

	@Bean
	public RedisServerBean redisServer() {
		return new RedisServerBean();
	}

	@Bean
	public RedisTemplate<String, RezscoreResult> redisTemplate(
			RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, RezscoreResult> temp = new RedisTemplate<>();
		temp.setConnectionFactory(connectionFactory);
		return temp;
	}

	@Bean
	public RedisCache<String, RezscoreResult> redisCache(
			RedisConnectionFactory connectionFactory,
			RedisTemplate<String, RezscoreResult> redisTemplate) {

		RedisCache<String, RezscoreResult> redisCache = new RedisCache<>(15,
				TimeUnit.SECONDS, redisTemplate);

		return redisCache;
	}

	/**
	 * Configuration
	 * http://stackoverflow.com/questions/29059794/embedded-redis-tries
	 * -to-connect-to-real-redis-server-resulting-in-an-exception <br>
	 * <br>
	 * Embedded Redis https://github.com/kstyrc/embedded-redis
	 */

	class RedisServerBean implements InitializingBean, DisposableBean {

		private RedisServer redisServer;

		@Override
		public void afterPropertiesSet() throws Exception {
			redisServer = new RedisServer(6377);
			redisServer.start();
		}

		@Override
		public void destroy() throws Exception {
			if (redisServer != null) {
				redisServer.stop();
			}
		}
	}
}

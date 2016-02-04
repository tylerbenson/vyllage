package documents.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import util.profiles.Profiles;
import documents.services.rezscore.RedisCache;
import documents.services.rezscore.result.RezscoreResult;

@Configuration(value = "documents.RedisProdCacheConfiguration")
@Profile({ Profiles.PROD })
public class RedisProdCacheConfiguration {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setPort(6379);
		jedisConnectionFactory.setUsePool(true);
		return jedisConnectionFactory;
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

		RedisCache<String, RezscoreResult> redisCache = new RedisCache<>(14,
				TimeUnit.DAYS, redisTemplate);

		return redisCache;
	}

}

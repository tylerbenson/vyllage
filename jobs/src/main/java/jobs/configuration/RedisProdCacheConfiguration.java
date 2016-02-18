package jobs.configuration;

import java.util.concurrent.TimeUnit;

import jobs.services.rezscore.RedisCache;
import jobs.services.rezscore.result.RezscoreResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;
import util.profiles.Profiles;

@Configuration(value = "jobs.RedisProdCacheConfiguration")
@Profile({ Profiles.PROD })
public class RedisProdCacheConfiguration {

	@Value("${spring.redis.port:6379}")
	private int port;

	@Value("${spring.redis.database:0}")
	private int index;

	@Value("${spring.redis.password:}")
	private String password;

	@Value("${spring.redis.host:localhost}")
	private String hostName;

	@Value("${spring.redis.pool.max-active:8}")
	private int maxActive;

	@Value("${spring.redis.pool.max-idle:8}")
	private int maxIdle;

	@Value("${spring.redis.pool.max-wait:-1}")
	private int maxWaitMillis;

	@Value("${spring.redis.pool.min-idle:8}")
	private int minIdle;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {

		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setPort(port);
		jedisConnectionFactory.setDatabase(index);
		jedisConnectionFactory.setPassword(password);
		jedisConnectionFactory.setHostName(hostName);

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(maxActive);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setMinIdle(minIdle);

		jedisConnectionFactory.setPoolConfig(poolConfig);

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

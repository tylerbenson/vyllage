package site;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.embedded.RedisServer;
import util.profiles.Profiles;

@Configuration
@Profile({ Profiles.TEST })
public class RedisTestSessionConfiguration {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setPort(6378);
		return jedisConnectionFactory;
	}

	@Bean
	public RedisServerBean redisServer() {
		return new RedisServerBean();
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
			redisServer = new RedisServer(6378);
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

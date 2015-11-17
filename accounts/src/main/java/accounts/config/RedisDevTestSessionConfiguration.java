package accounts.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.Protocol;
import redis.embedded.RedisServer;
import util.profiles.Profiles;

@Configuration
@Profile({ Profiles.DEV, Profiles.TEST })
public class RedisDevTestSessionConfiguration {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new JedisConnectionFactory();
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
			redisServer = new RedisServer(Protocol.DEFAULT_PORT);
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

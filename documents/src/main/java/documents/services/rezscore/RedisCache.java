package documents.services.rezscore;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * Our very own cache.
 * 
 * @author uh
 * @param <K>
 *            key to store and retrieve values
 * @param <V>
 *            value
 */
public class RedisCache<K, V> {

	private final long time;

	private final TimeUnit timeUnit;

	private final RedisTemplate<K, V> redisTemplate;

	public RedisCache(long time, TimeUnit timeUnit,
			RedisTemplate<K, V> redisTemplate) {
		this.time = time;
		this.timeUnit = timeUnit;
		this.redisTemplate = redisTemplate;
	}

	/**
	 * Returns the value associated with key in this cache, obtaining that value
	 * from valueLoader if necessary. No observable state associated with this
	 * cache is modified until loading completes. This method provides a simple
	 * substitute for the conventional
	 * "if cached, return; otherwise create, cache and return" pattern.
	 * 
	 * @param key
	 * @param valueLoader
	 * @return
	 * @throws Exception
	 */
	public V get(final K key, final Callable<? extends V> valueLoader)
			throws Exception {

		if (redisTemplate.hasKey(key))
			return redisTemplate.opsForValue().get(key);
		else
			return valueLoader.call();

	}

	public void put(K key, V value) {
		redisTemplate.opsForValue().set(key, value, time, timeUnit);
	}

}

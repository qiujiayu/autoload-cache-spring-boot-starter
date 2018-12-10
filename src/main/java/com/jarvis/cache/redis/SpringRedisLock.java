package com.jarvis.cache.redis;

import com.jarvis.cache.lock.AbstractRedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import redis.clients.jedis.Jedis;

@Slf4j
public class SpringRedisLock extends AbstractRedisLock {
    private final JedisConnectionFactory redisConnectionFactory;

    public SpringRedisLock(JedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * @param key
     * @return
     */
    public RedisConnection getRedisConnection(String key) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return null;
        }
        RedisConnection redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        // TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        return redisConnection;
    }

    /**
     * @param redisConnection
     */
    public void releaseConnection(RedisConnection redisConnection) {
        RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
    }

    @Override
    protected boolean setnx(String key, String val, int expire) {
        RedisConnection redisConnection = getRedisConnection(key);
        if (null == redisConnection) {
            return false;
        }
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return OK.equalsIgnoreCase(jedis.set(key, val, NX, EX, expire));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            releaseConnection(redisConnection);
        }
        return false;
    }

    @Override
    protected void del(String key) {
        RedisConnection redisConnection = getRedisConnection(key);
        if (null == redisConnection) {
            return;
        }
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            jedis.del(key);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            releaseConnection(redisConnection);
        }
    }

}

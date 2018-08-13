package com.jarvis.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.jarvis.cache.lock.AbstractRedisLock;

import redis.clients.jedis.Jedis;

public class SpringJedisLock extends AbstractRedisLock {
    private static final Logger logger = LoggerFactory.getLogger(SpringJedisLock.class);

    private final JedisConnectionFactory redisConnectionFactory;

    public SpringJedisLock(JedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    protected Long setnx(String key, String val) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return -1L;
        }
        RedisConnection redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return jedis.setnx(key, val);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
        return 0L;
    }

    @Override
    protected void expire(String key, int expire) {
        if (null == redisConnectionFactory || null == key || key.length() == 0 || expire < 0) {
            return;
        }
        RedisConnection redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            jedis.expire(key, expire);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }

    }

    @Override
    protected String get(String key) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return null;
        }
        RedisConnection redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return jedis.get(key);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
        return null;
    }

    @Override
    protected String getSet(String key, String newVal) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return null;
        }
        RedisConnection redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            return jedis.getSet(key, newVal);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
        return null;
    }

    @Override
    protected void del(String key) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return;
        }
        RedisConnection redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        try {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            jedis.del(key);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
    }

    public JedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

}

package com.jarvis.cache.redis;

import com.jarvis.cache.lock.AbstractRedisLock;
import com.jarvis.cache.serializer.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

public class SpringRedisLock extends AbstractRedisLock {
    private static final Logger logger = LoggerFactory.getLogger(SpringRedisLock.class);
    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

    private final RedisConnectionFactory redisConnectionFactory;

    public SpringRedisLock(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    private RedisConnection getConnection() {
        return RedisConnectionUtils.getConnection(redisConnectionFactory);
    }

    @Override
    protected Boolean setnx(String key, String val) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return false;
        }
        RedisConnection redisConnection = getConnection();
        try {
            return redisConnection.setNX(STRING_SERIALIZER.serialize(key), STRING_SERIALIZER.serialize(val));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
        return false;
    }

    @Override
    protected void expire(String key, int expire) {
        if (null == redisConnectionFactory || null == key || key.length() == 0 || expire < 0) {
            return;
        }
        RedisConnection redisConnection = getConnection();
        try {
            redisConnection.expire(STRING_SERIALIZER.serialize(key), expire);
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
        RedisConnection redisConnection = getConnection();
        try {
            return STRING_SERIALIZER.deserialize(redisConnection.get(STRING_SERIALIZER.serialize(key)), null);
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
        RedisConnection redisConnection = getConnection();
        try {
            return STRING_SERIALIZER.deserialize(redisConnection.getSet(STRING_SERIALIZER.serialize(key), STRING_SERIALIZER.serialize(newVal)), null);
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
        RedisConnection redisConnection = getConnection();
        try {
            redisConnection.del(STRING_SERIALIZER.serialize(key));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
    }

}

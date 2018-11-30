package com.jarvis.cache.redis;

import com.jarvis.cache.serializer.ISerializer;
import com.jarvis.cache.to.CacheKeyTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import java.util.Set;

/**
 * Redis缓存管理
 *
 * @author jiayu.qiu
 */
@Slf4j
public class SpringRedisCacheManager extends AbstractRedisCacheManager {

    private final RedisConnectionFactory redisConnectionFactory;

    public SpringRedisCacheManager(RedisConnectionFactory redisConnectionFactory, ISerializer<Object> serializer) {
        super(serializer);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    protected IRedis getRedis() {
        return new RedisConnectionClient(redisConnectionFactory);
    }

    public static class RedisConnectionClient implements IRedis {
        private final RedisConnectionFactory redisConnectionFactory;
        private final RedisConnection redisConnection;

        public RedisConnectionClient(RedisConnectionFactory redisConnectionFactory) {
            this.redisConnectionFactory = redisConnectionFactory;
            this.redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
            // TransactionSynchronizationManager.hasResource(redisConnectionFactory);
        }

        @Override
        public void close() {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }

        @Override
        public void set(byte[] key, byte[] value) {
            redisConnection.stringCommands().set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            redisConnection.stringCommands().setEx(key, seconds, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value) {
            redisConnection.hashCommands().hSet(key, field, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value, int seconds) {
            if (redisConnection.isPipelined()) {
                redisConnection.openPipeline();
            }
            try {
                redisConnection.hashCommands().hSet(key, field, value);
                redisConnection.keyCommands().expire(key, seconds);
            } finally {
                if (redisConnection.isPipelined()) {
                    redisConnection.closePipeline();
                }
            }
        }

        @Override
        public byte[] get(byte[] key) {
            return redisConnection.stringCommands().get(key);
        }

        @Override
        public byte[] hget(byte[] key, byte[] field) {
            return redisConnection.hashCommands().hGet(key, field);
        }

        @Override
        public void delete(Set<CacheKeyTO> keys) {
            if (redisConnection.isPipelined()) {
                redisConnection.openPipeline();
            }
            try {
                for (CacheKeyTO cacheKeyTO : keys) {
                    String cacheKey = cacheKeyTO.getCacheKey();
                    if (null == cacheKey || cacheKey.length() == 0) {
                        continue;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("delete cache {}", cacheKey);
                    }
                    String hfield = cacheKeyTO.getHfield();
                    if (null == hfield || hfield.length() == 0) {
                        redisConnection.keyCommands().del(KEY_SERIALIZER.serialize(cacheKey));
                    } else {
                        redisConnection.hashCommands().hDel(KEY_SERIALIZER.serialize(cacheKey), KEY_SERIALIZER.serialize(hfield));
                    }
                }
            } finally {
                if (redisConnection.isPipelined()) {
                    redisConnection.closePipeline();
                }
            }

        }


    }

}

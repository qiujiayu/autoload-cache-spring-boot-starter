package com.jarvis.cache.redis;

import com.jarvis.cache.serializer.ISerializer;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

/**
 * Redis缓存管理
 *
 * @author jiayu.qiu
 */
public class SpringRedisCacheManager extends AbstractRedisCacheManager {

    private final RedisConnectionFactory redisConnectionFactory;

    public SpringRedisCacheManager(RedisConnectionFactory redisConnectionFactory, ISerializer<Object> serializer) {
        super(serializer);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    protected IRedis getRedis(String cacheKey) {
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
            redisConnection.set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            redisConnection.setEx(key, seconds, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value) {
            redisConnection.hSet(key, field, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value, int seconds) {
            if (redisConnection.isPipelined()) {
                redisConnection.openPipeline();
            }
            redisConnection.hSet(key, field, value);
            redisConnection.expire(key, seconds);
            if (redisConnection.isPipelined()) {
                redisConnection.closePipeline();
            }
        }

        @Override
        public byte[] get(byte[] key) {
            return redisConnection.get(key);
        }

        @Override
        public byte[] hget(byte[] key, byte[] field) {
            return redisConnection.hGet(key, field);
        }

        @Override
        public void del(byte[] key) {
            redisConnection.del(key);
        }

        @Override
        public void hdel(byte[] key, byte[]... fields) {
            redisConnection.hDel(key, fields);
        }
    }

}

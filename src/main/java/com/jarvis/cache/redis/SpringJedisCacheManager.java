package com.jarvis.cache.redis;

import java.io.IOException;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import com.jarvis.cache.serializer.ISerializer;

import redis.clients.jedis.Jedis;

/**
 * Redis缓存管理
 * 
 * @author jiayu.qiu
 */
public class SpringJedisCacheManager extends AbstractRedisCacheManager {

    private final JedisConnectionFactory redisConnectionFactory;

    public SpringJedisCacheManager(JedisConnectionFactory redisConnectionFactory, ISerializer<Object> serializer) {
        super(serializer);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public JedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    @Override
    protected IRedis getRedis(String cacheKey) {
        return new JedisConnectionClient(redisConnectionFactory);
    }

    public static class JedisConnectionClient implements IRedis {
        private final JedisConnectionFactory redisConnectionFactory;
        private final RedisConnection redisConnection;
        private final Jedis jedis;

        public JedisConnectionClient(JedisConnectionFactory redisConnectionFactory) {
            this.redisConnectionFactory = redisConnectionFactory;
            this.redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
            this.jedis = (Jedis) redisConnection.getNativeConnection();
        }

        @Override
        public void close() throws IOException {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }

        @Override
        public void set(byte[] key, byte[] value) {
            jedis.set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            jedis.setex(key, seconds, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value) {
            jedis.hset(key, field, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value, int seconds) {
            jedis.hset(key, field, value);
            jedis.expire(key, seconds);
        }

        @Override
        public byte[] get(byte[] key) {
            return jedis.get(key);
        }

        @Override
        public byte[] hget(byte[] key, byte[] field) {
            return jedis.hget(key, field);
        }

        @Override
        public void del(byte[] key) {
            jedis.del(key);
        }

        @Override
        public void hdel(byte[] key, byte[]... fields) {
            jedis.hdel(key, fields);
        }
    }

}

package com.jarvis.cache.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.jarvis.cache.lock.ILock;
import com.jarvis.cache.lock.JedisClusterLock;
import com.jarvis.cache.redis.SpringJedisLock;

import redis.clients.jedis.JedisCluster;

/**
 * 对分布式锁进行一些默认配置<br>
 * 如果需要自定义，需要自行覆盖即可
 * 
 * @author: jiayu.qiu
 */
@Configuration
@AutoConfigureAfter({AutoloadCacheManageConfiguration.class })
public class DistributedLockConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockConfiguration.class);

    @Bean
    @ConditionalOnMissingBean({ILock.class})
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    public ILock autoLoadCacheDistributedLock(RedisConnectionFactory connectionFactory) {
        if (null == connectionFactory) {
            return null;
        }
        if (!(connectionFactory instanceof JedisConnectionFactory)) {
            logger.debug("connectionFactory is not JedisConnectionFactory");
            return null;
        }

        RedisConnection redisConnection = null;
        try {
            redisConnection = connectionFactory.getConnection();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        if (null != redisConnection) {
            if (redisConnection instanceof RedisClusterConnection) {
                RedisClusterConnection redisClusterConnection = (RedisClusterConnection) redisConnection;
                // 优先使用JedisCluster
                JedisCluster jedisCluster = null;
                jedisCluster = (JedisCluster) redisClusterConnection.getNativeConnection();
                if (null != jedisCluster) {
                    JedisClusterLock lock = new JedisClusterLock(jedisCluster);
                    logger.debug("ILock with JedisClusterLock auto-configured");
                    return lock;
                }
            } else if (redisConnection instanceof JedisConnection) {
                SpringJedisLock lock = new SpringJedisLock((JedisConnectionFactory) connectionFactory);
                logger.debug("ILock with SpringJedisLock auto-configured");
                return lock;
            }
        }
        return null;
    }

}

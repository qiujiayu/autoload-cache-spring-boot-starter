package com.jarvis.cache.autoconfigure;

import com.jarvis.cache.lock.AbstractRedisLock;
import com.jarvis.cache.lock.ILock;
import com.jarvis.cache.lock.JedisClusterLock;
import com.jarvis.cache.redis.SpringRedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisCluster;

/**
 * 对分布式锁进行一些默认配置<br>
 * 如果需要自定义，需要自行覆盖即可
 *
 * @author: jiayu.qiu
 */
@Slf4j
@Configuration
@AutoConfigureAfter({AutoloadCacheManageConfiguration.class})
public class DistributedLockConfiguration {


    @Bean
    @ConditionalOnMissingBean({ILock.class})
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    public ILock autoLoadCacheDistributedLock(RedisConnectionFactory connectionFactory) {
        if (null == connectionFactory) {
            return null;
        }

        RedisConnection redisConnection = null;
        try {
            redisConnection = connectionFactory.getConnection();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        AbstractRedisLock lock;
        if (redisConnection instanceof JedisClusterConnection) {
            JedisClusterConnection redisClusterConnection = (JedisClusterConnection) redisConnection;
            // 优先使用JedisCluster; 因为JedisClusterConnection 不支持eval、evalSha等方法需要使用JedisCluster
            JedisCluster jedisCluster = redisClusterConnection.getNativeConnection();
            lock = new JedisClusterLock(jedisCluster);
        } else {
            lock = new SpringRedisLock((JedisConnectionFactory) connectionFactory);
            if (log.isDebugEnabled()) {
                log.debug("ILock with SpringJedisLock auto-configured");
            }
        }
        return lock;
    }

}

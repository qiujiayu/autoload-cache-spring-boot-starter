package com.jarvis.cache.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.redis.JedisClusterCacheManager;
import com.jarvis.cache.script.AbstractScriptParser;
import com.jarvis.cache.script.OgnlParser;
import com.jarvis.cache.serializer.HessianSerializer;
import com.jarvis.cache.serializer.ISerializer;

import redis.clients.jedis.JedisCluster;

/**
 * 对autoload-cache进行一些默认配置<br>
 * 默认{@link AbstractScriptParser AbstractScriptParser} 使用Ognl表达式：{@link OgnlParser OgnlParser}<br>
 * 默认{@link ISerializer ISerializer} 使用Hessian 来处理：{@link HessianSerializer HessianSerializer}<br>
 * 默认只支持{@link JedisClusterCacheManager JedisClusterCacheManager}<br>
 * 如果需要自定义，只要自行覆盖即可
 * @author jiayu.qiu
 */
@Configuration
@ConditionalOnClass(ICacheManager.class)
@ConditionalOnMissingBean(value=ICacheManager.class)
@EnableConfigurationProperties(AutoloadCacheProperties.class)
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class AutoloadCacheManageConfiguration {

    private static final Logger logger=LoggerFactory.getLogger(AutoloadCacheManageConfiguration.class);

    @ConditionalOnMissingBean(AbstractScriptParser.class)
    @ConditionalOnClass(name="ognl.Ognl")
    @Bean
    public AbstractScriptParser scriptParser() {
        logger.debug("AbstractScriptParser auto-configured");
        return new OgnlParser();
    }

    @ConditionalOnMissingBean(ISerializer.class)
    @ConditionalOnClass(name="com.caucho.hessian.io.AbstractSerializerFactory")
    @Bean
    public ISerializer<Object> serializer() {// 推荐使用：Hessian
        logger.debug("ISerializer auto-configured");
        return new HessianSerializer();
    }

    @ConditionalOnMissingBean(ICacheManager.class)
    @Bean
    public ICacheManager cacheManager(AutoloadCacheProperties config, ISerializer<Object> serializer, RedisConnectionFactory connectionFactory) {
        if(null == connectionFactory) {
            return null;
        }
        JedisCluster jedisCluster=(JedisCluster)connectionFactory.getClusterConnection().getNativeConnection();
        if(null == jedisCluster) {
            return null;
        }
        JedisClusterCacheManager manager=new JedisClusterCacheManager(config.getConfig(), serializer);
        manager.setJedisCluster(jedisCluster);
        // 根据需要自行配置
        // manager.setHashExpire(hashExpire);
        // manager.setHashExpireByScript(hashExpireByScript);
        logger.debug("ICacheManager auto-configured," + config.getConfig());
        return manager;
    }
}

package com.jarvis.cache.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.ClassUtils;

import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.redis.JedisClusterCacheManager;
import com.jarvis.cache.redis.SpringJedisCacheManager;
import com.jarvis.cache.script.AbstractScriptParser;
import com.jarvis.cache.script.OgnlParser;
import com.jarvis.cache.script.SpringELParser;
import com.jarvis.cache.serializer.HessianSerializer;
import com.jarvis.cache.serializer.ISerializer;
import com.jarvis.cache.serializer.JdkSerializer;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

/**
 * 对autoload-cache进行一些默认配置<br>
 * 如果需要自定义，需要自行覆盖即可
 * @author jiayu.qiu
 */
@Slf4j
@Configuration
@ConditionalOnClass(name="com.jarvis.cache.ICacheManager")
@EnableConfigurationProperties(AutoloadCacheProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnProperty(value = "autoload.cache.enable", matchIfMissing = true)
public class AutoloadCacheManageConfiguration {

    private static final boolean ognlPresent=ClassUtils.isPresent("ognl.Ognl", AutoloadCacheManageConfiguration.class.getClassLoader());

    private static final boolean hessianPresent=ClassUtils.isPresent("com.caucho.hessian.io.AbstractSerializerFactory", AutoloadCacheManageConfiguration.class.getClassLoader());

    /**
     * 表达式解析器{@link AbstractScriptParser AbstractScriptParser} 注入规则：<br>
     * 如果导入了Ognl的jar包，优先 使用Ognl表达式：{@link OgnlParser OgnlParser}，否则使用{@link SpringELParser SpringELParser}<br>
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AbstractScriptParser.class)
    public AbstractScriptParser autoloadCacheScriptParser() {
        AbstractScriptParser res=null;
        if(ognlPresent) {
            res=new OgnlParser();
            log.debug("OgnlParser auto-configured");
        } else {
            res=new SpringELParser();
            log.debug("SpringELParser auto-configured");
        }

        return res;
    }

    /**
     * * 序列化工具{@link ISerializer ISerializer} 注入规则：<br>
     * 如果导入了Hessian的jar包，优先使用Hessian：{@link HessianSerializer HessianSerializer},否则使用{@link JdkSerializer JdkSerializer}<br>
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ISerializer.class)
    public ISerializer<Object> autoloadCacheSerializer() {
        ISerializer<Object> res;
        if(hessianPresent) {// 推荐优先使用：Hessian
            res=new HessianSerializer();
            log.debug("HessianSerializer auto-configured");
        } else {
            res=new JdkSerializer();
            log.debug("JdkSerializer auto-configured");
        }
        return res;
    }

    /**
     * 默认只支持{@link JedisClusterCacheManager JedisClusterCacheManager}<br>
     * @param config
     * @param serializer
     * @param applicationContext
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ICacheManager.class)
    @ConditionalOnClass(name="org.springframework.data.redis.connection.RedisConnectionFactory")
    public ICacheManager autoloadCacheCacheManager(AutoloadCacheProperties config, ISerializer<Object> serializer, ApplicationContext applicationContext) {
        RedisConnectionFactory connectionFactory = null;
        try {
            connectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if(null == connectionFactory) {
            return null;
        }
        if(!(connectionFactory instanceof JedisConnectionFactory)) {
            log.debug("connectionFactory is not JedisConnectionFactory");
            return null;
        }

        RedisConnection redisConnection=null;
        try {
            redisConnection=connectionFactory.getConnection(); // 当Redis配置不正确时，此处会抛异常
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
        }
        if(null != redisConnection) {
            if(redisConnection instanceof RedisClusterConnection) {
                RedisClusterConnection redisClusterConnection=(RedisClusterConnection)redisConnection;
                // 优先使用JedisCluster
                JedisCluster jedisCluster=null;
                jedisCluster=(JedisCluster)redisClusterConnection.getNativeConnection();
                if(null != jedisCluster) {
                    JedisClusterCacheManager manager=new JedisClusterCacheManager(serializer);
                    manager.setJedisCluster(jedisCluster);
                    // 根据需要自行配置
                    manager.setHashExpire(config.getJedis().getHashExpire());
                    manager.setHashExpireByScript(config.getJedis().isHashExpireByScript());
                    log.debug("ICacheManager with JedisClusterCacheManager auto-configured," + config.getConfig());
                    return manager;
                }
            } else if(redisConnection instanceof JedisConnection) {
                SpringJedisCacheManager manager=new SpringJedisCacheManager(serializer);
                manager.setRedisConnectionFactory((JedisConnectionFactory)connectionFactory);
                // 根据需要自行配置
                manager.setHashExpire(config.getJedis().getHashExpire());
                manager.setHashExpireByScript(config.getJedis().isHashExpireByScript());
                log.debug("ICacheManager with SpringJedisCacheManager auto-configured," + config.getConfig());
                return manager;
            }
        }
        return null;
    }
}

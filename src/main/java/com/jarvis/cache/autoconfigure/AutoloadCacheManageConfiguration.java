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
import org.springframework.util.ClassUtils;

import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.redis.JedisClusterCacheManager;
import com.jarvis.cache.script.AbstractScriptParser;
import com.jarvis.cache.script.OgnlParser;
import com.jarvis.cache.script.SpringELParser;
import com.jarvis.cache.serializer.HessianSerializer;
import com.jarvis.cache.serializer.ISerializer;
import com.jarvis.cache.serializer.JdkSerializer;

import redis.clients.jedis.JedisCluster;

/**
 * 对autoload-cache进行一些默认配置<br>
 * 表达式解析器{@link AbstractScriptParser AbstractScriptParser} 注入规则：<br>
 * 如果导入了Ognl的jar包，优先 使用Ognl表达式：{@link OgnlParser OgnlParser}，否则使用{@link SpringELParser SpringELParser}<br>
 * 序列化工具{@link ISerializer ISerializer} 注入规则：<br>
 * 如果导入了Hessian的jar包，优先使用Hessian：{@link HessianSerializer HessianSerializer},否则使用{@link JdkSerializer JdkSerializer}<br>
 * 默认只支持{@link JedisClusterCacheManager JedisClusterCacheManager}<br>
 * 如果需要自定义，只要自行覆盖即可
 * @author jiayu.qiu
 */
@Configuration
@ConditionalOnClass(ICacheManager.class)
@EnableConfigurationProperties(AutoloadCacheProperties.class)
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class AutoloadCacheManageConfiguration {

    private static final Logger logger=LoggerFactory.getLogger(AutoloadCacheManageConfiguration.class);

    private static final boolean ognlPresent=ClassUtils.isPresent("ognl.Ognl", AutoloadCacheManageConfiguration.class.getClassLoader());

    private static final boolean hessianPresent=ClassUtils.isPresent("com.caucho.hessian.io.AbstractSerializerFactory", AutoloadCacheManageConfiguration.class.getClassLoader());

    
    /**
     * 
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AbstractScriptParser.class)
    public AbstractScriptParser scriptParser() {
        AbstractScriptParser res=null;
        if(ognlPresent) {
            res=new OgnlParser();
            logger.debug("OgnlParser auto-configured");
        } else {
            res=new SpringELParser();
            logger.debug("SpringELParser auto-configured");
        }

        return res;
    }

    /**
     * 
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ISerializer.class)
    public ISerializer<Object> serializer() {
        ISerializer<Object> res;
        if(hessianPresent) {// 推荐优先使用：Hessian
            res=new HessianSerializer();
            logger.debug("HessianSerializer auto-configured");
        } else {
            res=new JdkSerializer();
            logger.debug("JdkSerializer auto-configured");
        }
        return res;
    }

    
    /**
     * 
     * @param config
     * @param serializer
     * @param connectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ICacheManager.class)
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
        manager.setHashExpire(config.getJedisCluster().getHashExpire());
        manager.setHashExpireByScript(config.getJedisCluster().isHashExpireByScript());
        logger.debug("ICacheManager auto-configured," + config.getConfig());
        return manager;
    }
}

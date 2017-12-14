package com.jarvis.cache.autoconfigure;

import javax.annotation.PostConstruct;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.clone.ICloner;
import com.jarvis.cache.interceptor.CacheDeleteInterceptor;
import com.jarvis.cache.interceptor.CacheDeleteTransactionalInterceptor;
import com.jarvis.cache.interceptor.CacheMethodInterceptor;
import com.jarvis.cache.script.AbstractScriptParser;
import com.jarvis.cache.serializer.ISerializer;

/**
 * 对autoload-cache进行自动配置<br>
 * 需要先完成 {@link AutoloadCacheManageConfiguration AutoloadCacheManageConfiguration}的配置<br>
 * 然后执行此类中的AOP相关的配置<br>
 * @author jiayu.qiu
 */
@Configuration
@ConditionalOnClass(name="com.jarvis.cache.CacheHandler")
@AutoConfigureAfter(AutoloadCacheManageConfiguration.class)
@ConditionalOnProperty(value = "autoload.cache.enable", matchIfMissing = true)
public class AutoloadCacheAutoConfigure {

    private static final String VALIDATOR_BEAN_NAME="autoloadCacheAutoConfigurationValidator";

    @Autowired
    private AutoloadCacheProperties config;

    @Bean(name=VALIDATOR_BEAN_NAME)
    public CacheManagerValidator autoloadCacheAutoConfigurationValidator() {
        return new CacheManagerValidator();
    }

    @ConditionalOnMissingBean(CacheHandler.class)
    @ConditionalOnBean({ICacheManager.class, AbstractScriptParser.class, ICloner.class})
    @Bean(destroyMethod="destroy")
    public CacheHandler autoloadCacheHandler(ICacheManager cacheManager, AbstractScriptParser scriptParser, ICloner cloner ) {
        return new CacheHandler(cacheManager, scriptParser, config.getConfig(), cloner);
    }

    // 1. 创建通知
    @Bean
    @ConditionalOnBean(CacheHandler.class)
    public CacheMethodInterceptor autoloadCacheMethodInterceptor(CacheHandler cacheHandler) {
        return new CacheMethodInterceptor(cacheHandler, config);
    }

    @Bean
    @ConditionalOnBean(CacheHandler.class)
    public CacheDeleteInterceptor autoloadCacheDeleteInterceptor(CacheHandler cacheHandler) {
        return new CacheDeleteInterceptor(cacheHandler, config);
    }

    @Bean("autoloadCacheDeleteTransactionalInterceptor")
    @ConditionalOnBean(CacheHandler.class)
    public CacheDeleteTransactionalInterceptor autoloadCacheDeleteTransactionalInterceptor(CacheHandler cacheHandler) {
        return new CacheDeleteTransactionalInterceptor(cacheHandler, config);
    }

    // 2.配置Advisor
    @Bean("autoloadCacheAdvisor")
    @ConditionalOnBean(CacheHandler.class)
    public AbstractPointcutAdvisor autoloadCacheAdvisor(CacheMethodInterceptor cacheMethodInterceptor) {
        AbstractPointcutAdvisor cacheAdvisor=new MethodAnnotationPointcutAdvisor(Cache.class, cacheMethodInterceptor);
        cacheAdvisor.setOrder(config.getCacheOrder());
        return cacheAdvisor;
    }

    @Bean("autoloadCacheDeleteAdvisor")
    @ConditionalOnBean(CacheHandler.class)
    public AbstractPointcutAdvisor autoloadCacheDeleteAdvisor(CacheDeleteInterceptor cacheDeleteInterceptor) {
        AbstractPointcutAdvisor cacheDeleteAdvisor=new MethodAnnotationPointcutAdvisor(CacheDelete.class, cacheDeleteInterceptor);
        cacheDeleteAdvisor.setOrder(config.getDeleteCacheOrder());
        return cacheDeleteAdvisor;
    }

    @Bean("autoloadCacheDeleteTransactionalAdvisor")
    @ConditionalOnBean(CacheHandler.class)
    public AbstractPointcutAdvisor autoloadCacheDeleteTransactionalAdvisor(@Qualifier("autoloadCacheDeleteTransactionalInterceptor") CacheDeleteTransactionalInterceptor cacheDeleteTransactionalInterceptor) {
        AbstractPointcutAdvisor cacheDeleteTransactionalAdvisor=new MethodAnnotationPointcutAdvisor(CacheDeleteTransactional.class, cacheDeleteTransactionalInterceptor);
        cacheDeleteTransactionalAdvisor.setOrder(config.getDeleteCacheTransactionalOrder());
        return cacheDeleteTransactionalAdvisor;
    }

    // 3.配置ProxyCreator
    @Bean
    @ConditionalOnBean(CacheHandler.class)
    public AbstractAdvisorAutoProxyCreator autoloadCacheAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxy=new DefaultAdvisorAutoProxyCreator();
        proxy.setAdvisorBeanNamePrefix("autoloadCache");
        proxy.setProxyTargetClass(config.isProxyTargetClass());
        // proxy.setInterceptorNames("cacheAdvisor","cacheDeleteAdvisor","cacheDeleteTransactionalAdvisor");// 注意此处不需要设置，否则会执行两次
        return proxy;
    }

    static class CacheManagerValidator {

        @Autowired(required=false)
        private AbstractScriptParser scriptParser;

        @Autowired(required=false)
        private ISerializer<Object> serializer;

        @Autowired(required=false)
        private ICacheManager cacheManager;

        @PostConstruct
        public void checkHasCacheManager() {
            Assert.notNull(this.scriptParser, "No script parser could be auto-configured");
            Assert.notNull(this.serializer, "No serializer could be auto-configured");
            Assert.notNull(this.cacheManager, "No cache manager could be auto-configured");
        }

    }
}

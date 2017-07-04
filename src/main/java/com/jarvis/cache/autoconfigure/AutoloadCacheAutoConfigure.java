package com.jarvis.cache.autoconfigure;

import javax.annotation.PostConstruct;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
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
@ConditionalOnClass(com.jarvis.cache.CacheHandler.class)
@AutoConfigureAfter({AutoloadCacheManageConfiguration.class})
public class AutoloadCacheAutoConfigure {

    private static final String VALIDATOR_BEAN_NAME="autoloadCacheAutoConfigurationValidator";

    @Bean(name=VALIDATOR_BEAN_NAME)
    public CacheManagerValidator autoloadCacheAutoConfigurationValidator() {
        return new CacheManagerValidator();
    }

    @ConditionalOnMissingBean(CacheHandler.class)
    @ConditionalOnBean({ICacheManager.class, AbstractScriptParser.class})
    @Bean(destroyMethod="destroy")
    public CacheHandler cacheHandler(ICacheManager cacheManager, AbstractScriptParser scriptParser) {
        return new CacheHandler(cacheManager, scriptParser);
    }

    // 1. 创建通知
    @Bean
    public CacheMethodInterceptor cacheMethodInterceptor(CacheHandler cacheHandler) {
        return new CacheMethodInterceptor(cacheHandler);
    }

    @Bean
    public CacheDeleteInterceptor cacheDeleteInterceptor(CacheHandler cacheHandler) {
        return new CacheDeleteInterceptor(cacheHandler);
    }

    @Bean
    public CacheDeleteTransactionalInterceptor cacheDeleteTransactionalInterceptor(CacheHandler cacheHandler) {
        return new CacheDeleteTransactionalInterceptor(cacheHandler);
    }

    // 2.配置Advisor
    @Bean("autoloadCacheAdvisor")
    public AbstractPointcutAdvisor cacheAdvisor(CacheMethodInterceptor cacheMethodInterceptor) {
        AbstractPointcutAdvisor cacheAdvisor=new MethodAnnotationPointcutAdvisor(Cache.class, cacheMethodInterceptor);
        cacheAdvisor.setOrder(0);
        return cacheAdvisor;
    }

    @Bean("autoloadCacheDeleteAdvisor")
    public AbstractPointcutAdvisor cacheDeleteAdvisor(CacheDeleteInterceptor cacheDeleteInterceptor) {
        AbstractPointcutAdvisor cacheDeleteAdvisor=new MethodAnnotationPointcutAdvisor(CacheDelete.class, cacheDeleteInterceptor);
        cacheDeleteAdvisor.setOrder(Integer.MAX_VALUE);
        return cacheDeleteAdvisor;
    }

    @Bean("autoloadCacheDeleteTransactionalAdvisor")
    public AbstractPointcutAdvisor cacheDeleteTransactionalAdvisor(CacheDeleteTransactionalInterceptor cacheDeleteTransactionalInterceptor) {
        AbstractPointcutAdvisor cacheDeleteTransactionalAdvisor=new MethodAnnotationPointcutAdvisor(CacheDeleteTransactional.class, cacheDeleteTransactionalInterceptor);
        cacheDeleteTransactionalAdvisor.setOrder(0);
        return cacheDeleteTransactionalAdvisor;
    }

    // 3.配置ProxyCreator
    @ConditionalOnMissingBean(DefaultAdvisorAutoProxyCreator.class)
    // @Lazy
    @Bean
    public AbstractAdvisorAutoProxyCreator autoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxy=new DefaultAdvisorAutoProxyCreator();
        proxy.setAdvisorBeanNamePrefix("autoloadCache");
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

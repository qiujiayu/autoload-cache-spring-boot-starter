package com.jarvis.cache.autoconfigure.enable;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.interceptor.CacheDeleteInterceptor;
import com.jarvis.cache.interceptor.CacheDeleteTransactionalInterceptor;
import com.jarvis.cache.interceptor.CacheMethodInterceptor;

@ConditionalOnClass(com.jarvis.cache.CacheHandler.class)
@EnableConfigurationProperties(CacheProperties.class)
public class AutoloadCacheConfiguration {

    // 1. 创建通知
    @Autowired
    private CacheMethodInterceptor cacheMethodInterceptor;

    @Autowired
    private CacheDeleteInterceptor cacheDeleteInterceptor;

    @Autowired
    private CacheDeleteTransactionalInterceptor cacheDeleteTransactionalInterceptor;

    // 2.
    @Bean("cacheAdvisor")
    public AbstractPointcutAdvisor cacheAdvisor() {
        return new MethodAnnotationPointcutAdvisor(Cache.class, cacheMethodInterceptor);
    }

    @Bean("cacheDeleteAdvisor")
    public AbstractPointcutAdvisor cacheDeleteAdvisor() {
        return new MethodAnnotationPointcutAdvisor(CacheDelete.class, cacheDeleteInterceptor);
    }

    @Bean("cacheDeleteTransactionalAdvisor")
    public AbstractPointcutAdvisor cacheDeleteTransactionalAdvisor() {
        return new MethodAnnotationPointcutAdvisor(CacheDeleteTransactional.class, cacheDeleteTransactionalInterceptor);
    }

    // 3.
    @Bean
    public DefaultAdvisorAutoProxyCreator proxyFactoryBean() {
        DefaultAdvisorAutoProxyCreator proxy=new DefaultAdvisorAutoProxyCreator();
        // proxy.setInterceptorNames("cacheAdvisor","cacheDeleteAdvisor","cacheDeleteTransactionalAdvisor");// 注意此处不需要设置，否则会执行两次
        return proxy;
    }
}

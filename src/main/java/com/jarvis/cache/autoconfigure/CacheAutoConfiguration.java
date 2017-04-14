package com.jarvis.cache.autoconfigure;

import javax.annotation.PostConstruct;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.jarvis.cache.annotation.Cache;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration extends AbstractPointcutAdvisor {

    private static final long serialVersionUID=-6261545391290879109L;

    private static final Logger logger=LoggerFactory.getLogger(CacheAutoConfiguration.class);

    private Pointcut pointcut;

    private Advice advice;

    @Autowired
    private CacheProperties logProperties;

    @PostConstruct
    public void init() {
        logger.info("init LogAutoConfiguration start");
        this.pointcut=new AnnotationMatchingPointcut(null, Cache.class);
        this.advice=new CacheMethodInterceptor();
        logger.info("init LogAutoConfiguration end");
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}

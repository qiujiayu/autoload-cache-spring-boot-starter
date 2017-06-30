package com.jarvis.cache.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.interceptor.aopproxy.CacheAopProxy;

@Component
public class CacheDeleteTransactionalInterceptor implements MethodInterceptor {

    private static final Logger logger=LoggerFactory.getLogger(CacheDeleteTransactionalInterceptor.class);

    @Autowired
    private CacheHandler cacheHandler;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method=invocation.getMethod();
        if(method.isAnnotationPresent(CacheDeleteTransactional.class)) {
            CacheDeleteTransactional cacheDeleteTransactional=method.getAnnotation(CacheDeleteTransactional.class);
            return cacheHandler.proceedDeleteCacheTransactional(new CacheAopProxy(invocation), cacheDeleteTransactional);
        }

        return invocation.proceed();
    }

}

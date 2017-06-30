package com.jarvis.cache.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.interceptor.aopproxy.CacheAopProxy;

@Component
public class CacheMethodInterceptor implements MethodInterceptor {

    private static final Logger logger=LoggerFactory.getLogger(CacheMethodInterceptor.class);

    @Autowired
    private CacheHandler cacheHandler;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method=invocation.getMethod();
        if(method.isAnnotationPresent(Cache.class)) {
            Cache cache=method.getAnnotation(Cache.class);
            return cacheHandler.proceed(new CacheAopProxy(invocation), cache);
        }

        return invocation.proceed();
    }

}

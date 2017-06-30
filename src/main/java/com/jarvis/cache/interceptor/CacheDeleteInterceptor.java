package com.jarvis.cache.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.interceptor.aopproxy.DeleteCacheAopProxy;

@Component
public class CacheDeleteInterceptor implements MethodInterceptor {

    private static final Logger logger=LoggerFactory.getLogger(CacheDeleteInterceptor.class);

    @Autowired
    private CacheHandler cacheHandler;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result=invocation.proceed();
        Method method=invocation.getMethod();
        if(method.isAnnotationPresent(CacheDelete.class)) {
            CacheDelete cacheDelete=method.getAnnotation(CacheDelete.class);
            cacheHandler.deleteCache(new DeleteCacheAopProxy(invocation), cacheDelete, result);
        }
        return result;
    }

}

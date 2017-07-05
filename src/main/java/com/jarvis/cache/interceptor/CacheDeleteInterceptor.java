package com.jarvis.cache.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.interceptor.aopproxy.DeleteCacheAopProxy;

/**
 * 对@CacheDelete 拦截注解
 * @author jiayu.qiu
 */
public class CacheDeleteInterceptor implements MethodInterceptor {

    private static final Logger logger=LoggerFactory.getLogger(CacheDeleteInterceptor.class);

    private final CacheHandler cacheHandler;

    public CacheDeleteInterceptor(CacheHandler cacheHandler) {
        this.cacheHandler=cacheHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result=invocation.proceed();
        Method method=invocation.getMethod();
        if(method.isAnnotationPresent(CacheDelete.class)) {
            CacheDelete cacheDelete=method.getAnnotation(CacheDelete.class);
            logger.debug(invocation.getThis().getClass().getName() + "." + method.getName() + "-->@CacheDelete");
            cacheHandler.deleteCache(new DeleteCacheAopProxy(invocation), cacheDelete, result);
        }
        return result;
    }

}

package com.jarvis.cache.autoconfigure;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheMethodInterceptor implements MethodInterceptor {

    private static final Logger logger=LoggerFactory.getLogger(CacheMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName=invocation.getMethod().getName();
        long start=System.currentTimeMillis();
        Object result=invocation.proceed();
        long end=System.currentTimeMillis();
        logger.info("====method({}), cost({}) ", methodName, (end - start));
        return result;
    }

}

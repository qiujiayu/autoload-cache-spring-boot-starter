package com.jarvis.cache.interceptor.aopproxy;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import com.jarvis.cache.aop.DeleteCacheAopProxyChain;

/**
 * @author jiayu.qiu
 */
public class DeleteCacheAopProxy implements DeleteCacheAopProxyChain {

    private final MethodInvocation invocation;

    private Method method;

    public DeleteCacheAopProxy(MethodInvocation invocation) {
        this.invocation=invocation;
    }

    @Override
    public Object[] getArgs() {
        return invocation.getArguments();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getTargetClass() {
        return invocation.getThis().getClass();
    }

    @Override
    public Method getMethod() {
        if(null == method) {
            this.method=invocation.getMethod();
        }
        return method;
    }

}

package de.mnet.migration.common.util;

import java.io.*;
import java.util.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Caches method result if method was configured (in spring XML) for caching.
 */
public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean, Serializable {

    @Autowired
    private transient CacheManager cacheManager;


    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cacheManager, "A cache-manager is required. Use Manager(CacheManager) to provide one.");
    }


    /**
     * Main method caches method result if method is configured for caching. Method results must be Serializable.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        Cache cache = getCache(invocation);
        Element element = null;
        CacheKey cacheKey = new CacheKey(invocation.getArguments());
        if (cache != null) {
            element = cache.get(cacheKey);
        }
        if (element == null) {
            // call target/sub-interceptor
            result = invocation.proceed();

            boolean isEmptyCollection = false;
            if ((result != null) && (result instanceof Collection<?>) && ((Collection<?>) result).isEmpty()) {
                isEmptyCollection = true;
            }

            if ((cache != null) && (result != null) && !isEmptyCollection) {
                element = new Element(cacheKey, (Serializable) result);
                cache.put(element);
            }
        }
        else {
            result = element.getValue();
        }
        return result;
    }


    /**
     * Creates cache name: {@code targetName.methodName(argument1-class-name, argument2-class-name...)} or {@code
     * targetName.methodName()} if argumentsClasses is null or empty
     */
    private Cache getCache(MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();
        Class<?>[] argumentsClasses = invocation.getMethod().getParameterTypes();

        Class<?> clazz = invocation.getThis().getClass();
        String name = getCacheName(clazz, methodName, argumentsClasses);
        Cache cache = cacheManager.getCache(name);
        if (cache == null) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; (i < interfaces.length) && (cache == null); i++) {
                name = getCacheName(interfaces[i], methodName, argumentsClasses);
                cache = cacheManager.getCache(name);
            }
        }
        return cache;
    }


    private String getCacheName(Class<?> clazz, String methodName, Class<?>[] argumentsClasses) {
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName()).append(".").append(methodName).append("(");
        if ((argumentsClasses != null) && (argumentsClasses.length != 0)) {
            for (int i = 0; i < argumentsClasses.length; i++) {
                sb.append(argumentsClasses[i].getName());
                if ((i + 1) < argumentsClasses.length) {
                    sb.append(",");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }


    private class CacheKey implements Serializable {
        Object[] arguments;

        public CacheKey(Object[] args) {
            arguments = args;
        }

        private MethodCacheInterceptor getOuterType() {
            return MethodCacheInterceptor.this;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + Arrays.hashCode(arguments);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (!Arrays.equals(arguments, other.arguments)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CacheKey [args: " + Arrays.toString(arguments) + "]";
        }
    }
}

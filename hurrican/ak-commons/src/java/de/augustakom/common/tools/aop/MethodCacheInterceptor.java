package de.augustakom.common.tools.aop;

import java.io.*;
import java.util.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean, Serializable {

    private static final long serialVersionUID = 3290630453032879134L;
    private transient CacheManager cacheManager;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cacheManager, "A cache-manager is required. Use Manager(CacheManager) to provide one.");
    }

    /**
     * main method caches method result if method is configured for caching method results must be serializable
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result;
        Cache cache = getCache(invocation);
        Element element = null;
        CacheKey cacheKey = new CacheKey(invocation.getArguments(), this);
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
     * Creates cache name: targetName.methodName(argument1-class-name,argument2-class-name...) if argumentsClasses is
     * null or empty name is: targetName.methodName()
     */
    private Cache getCache(MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();
        Class<?>[] argumentsClasses = invocation.getMethod().getParameterTypes();

        Class<?> clazz = invocation.getThis().getClass();
        String name = getCacheName(clazz, methodName, argumentsClasses);
        Cache cache = getCacheManager().getCache(name);
        if (cache == null) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; (i < interfaces.length) && (cache == null); i++) {
                name = getCacheName(interfaces[i], methodName, argumentsClasses);
                cache = getCacheManager().getCache(name);
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

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    private static class CacheKey implements Serializable {
        private static final long serialVersionUID = -5744133273173694256L;
        private final MethodCacheInterceptor methodCacheInterceptor;
        Object[] arguments;


        public CacheKey(Object[] args, MethodCacheInterceptor methodCacheInterceptor) {
            arguments = args;
            this.methodCacheInterceptor = methodCacheInterceptor;
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + methodCacheInterceptor.hashCode();
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
            return methodCacheInterceptor.equals(other.methodCacheInterceptor) && Arrays.equals(arguments, other.arguments);
        }

        @Override
        public String toString() {
            return "CacheKey [arguments=" + Arrays.toString(arguments) + "]";
        }

    }

}

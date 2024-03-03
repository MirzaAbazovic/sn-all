package de.bitconex.adlatus.common.infrastructure.aspects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    @Pointcut("@annotation(de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging) && execution(public * *(..))")
    public void annotatedRestMethods() {
        // pointcut definition
    }

    @Pointcut("within(@de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging *) && execution(public * *(..))")
    public void annotatedRestClasses() {
        // pointcut definition
    }

    @Around("annotatedRestMethods() || annotatedRestClasses()")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodName = joinPoint.getSignature().getName()+ "()";
        log.info("External request started [Service={}/{}]", "REST", methodName);
        final StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            return joinPoint.proceed(); // the inner call
        } catch (final RuntimeException e) {
            log.error("Exception occurred during external request [Service={}/{}]", "REST", methodName, e);
            throw e;
        } finally {
            stopWatch.stop();
            log.info("External request finished [Service={}/{}], Duration={} ms", "REST", methodName, stopWatch.getTime());
        }

    }
}

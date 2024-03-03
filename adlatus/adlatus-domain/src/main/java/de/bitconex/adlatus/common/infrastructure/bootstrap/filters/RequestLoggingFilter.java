package de.bitconex.adlatus.common.infrastructure.bootstrap.filters;

import de.bitconex.adlatus.common.infrastructure.bootstrap.BootstrapUtils;
import de.bitconex.adlatus.common.dto.constants.LoggingConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RequestLoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //This is intentionally left empty
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();
        MDC.put(LoggingConstants.LOG_INFO_REQUEST_URI, requestUri);
        MDC.put(LoggingConstants.LOG_INFO_REQUEST_ID, UUID.randomUUID().toString().substring(0, 8));
        MDC.put(LoggingConstants.LOG_CLIENT_IP, BootstrapUtils.extractIpAddress(httpRequest));

        String httpMethod = (httpRequest).getMethod();
        MDC.put(LoggingConstants.LOG_HTTP_METHOD, httpMethod);

        long startTime = System.nanoTime();
        boolean logRequest = logRequest(httpRequest.getRequestURI());

        if (logRequest) {
            log.info("Request started [Path={}]", requestUri);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            if (logRequest) {
                long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                int responseStatus = ((HttpServletResponse) response).getStatus();
                log.info("Request ended [Path={} Duration={} ms Response-Status={}]", requestUri, elapsedTime, responseStatus);
            }
            MDC.remove(LoggingConstants.LOG_INFO_REQUEST_URI);
            MDC.remove(LoggingConstants.LOG_INFO_REQUEST_ID);
            MDC.remove(LoggingConstants.LOG_CLIENT_IP);
            MDC.remove(LoggingConstants.LOG_HTTP_METHOD);
        }
    }

    private boolean logRequest(String url) {
        return !url.startsWith("/public"); // add ignored paths here
    }

    @Override
    public void destroy() {
        //This is intentionally left empty
    }
}

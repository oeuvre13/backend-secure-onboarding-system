package com.reg.regis.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig implements HandlerInterceptor {
    
    @Value("${app.rateLimit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${app.rateLimit.capacity:5}")
    private long capacity;
    
    @Value("${app.rateLimit.refillRate:1}")
    private long refillRate;
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull Object handler) throws Exception {
        if (!rateLimitEnabled) {
            return true;
        }
        
        String clientId = getClientId(request);
        Bucket bucket = buckets.computeIfAbsent(clientId, this::createBucket);
        
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            } catch (Exception e) {
                // Log error if needed
                System.err.println("Error writing rate limit response: " + e.getMessage());
            }
            return false;
        }
    }
    
    private String getClientId(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    private Bucket createBucket(String clientId) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillRate, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
package ro.uaic.ossp.monitors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitor {

    private long THRESHOLD = 3000; // 3 seconds

    @Around("execution(* ro.uaic.ossp.services.GradeService.uploadCsv(..))")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time = System.currentTimeMillis() - start;

        if (time > THRESHOLD) {
            System.out.println("[PerformanceMonitor] Upload too slow → consider batching");
            // Auto-resolve example: reduce batch size globally
            System.setProperty("spring.jpa.properties.hibernate.jdbc.batch_size", "10");
        }

        return result;
    }
}
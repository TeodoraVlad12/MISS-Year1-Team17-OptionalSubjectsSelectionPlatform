package ro.uaic.ossp.monitors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionMonitor {

    @Around("execution(* ro.uaic.ossp.services.GradeService.uploadCsv(..))")
    public Object retryOnFailure(ProceedingJoinPoint pjp) throws Throwable {

        try {
            return pjp.proceed();
        } catch (Exception ex) {
            System.out.println("[TransactionMonitor] Upload failed, retrying...");
            return pjp.proceed(); // auto-retry once
        }
    }
}


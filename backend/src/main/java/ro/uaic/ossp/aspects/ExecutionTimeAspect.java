package ro.uaic.ossp.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    /**
     * Complex AOP Aspect for logging, error tracking and timing of
     * all strategy method executions under `services.strategies.implementations`.
     *
     * Features:
     *  - Logs method start, arguments, and execution time
     *  - Captures and logs exceptions with full stack trace
     *  - Safely handles null arguments and large collections
     *  - Uses nanoTime() for more precise duration measurement
     *  - Does NOT interfere with the target method flow
     */
    @Around("execution(* ro.uaic.ossp.services.strategies.implementations.*.*(..))")
    public Object traceAndMeasure(ProceedingJoinPoint joinPoint) throws Throwable {

        // Extract method and class info
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        // Collect arguments
        Object[] args = joinPoint.getArgs();
        String formattedArgs = Arrays.toString(
                Arrays.stream(args)
                        .map(arg -> {
                            if (arg == null) return "null";
                            String s = arg.toString();
                            return s.length() > 150 ? s.substring(0, 150) + "...(truncated)" : s;
                        })
                        .toArray()
        );

        long start = System.nanoTime();
        log.info("▶ START [{}#{}] with args: {}", className, methodName, formattedArgs);

        try {
            // Proceed with the actual method call
            Object result = joinPoint.proceed();

            long duration = (System.nanoTime() - start) / 1_000_000; // convert ns -> ms

            // Handle long results more compactly
            String resultSummary;
            if (result == null) {
                resultSummary = "null";
            } else if (result instanceof java.util.Collection<?> col) {
                resultSummary = String.format("Collection(size=%d)", col.size());
            } else {
                String text = result.toString();
                resultSummary = text.length() > 200 ? text.substring(0, 200) + "...(truncated)" : text;
            }

            log.info("END [{}#{}] executed in {} ms | Result: {}", className, methodName, duration, resultSummary);
            return result;

        } catch (Exception ex) {
            long duration = (System.nanoTime() - start) / 1_000_000;
            log.error("ERROR in [{}#{}] after {} ms | Exception: {} - {}",
                    className, methodName, duration,
                    ex.getClass().getSimpleName(), ex.getMessage(), ex);

            // Re-throw exception to preserve application flow
            throw ex;
        }
    }
}

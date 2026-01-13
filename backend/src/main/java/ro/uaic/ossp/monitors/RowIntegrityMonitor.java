package ro.uaic.ossp.monitors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RowIntegrityMonitor {

    @Before("execution(* ro.uaic.ossp.services.GradeService.splitCsvLine(..)) && args(line)")
    public void checkRow(String line) {
        if (line.split(",").length < 3) {
            System.out.println("[RowIntegrityMonitor] Skipping malformed row: " + line);
        }
    }
}


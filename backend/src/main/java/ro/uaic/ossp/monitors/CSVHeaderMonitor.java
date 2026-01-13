package ro.uaic.ossp.monitors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.utils.CustomMultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Aspect
@Component
public class CSVHeaderMonitor {

    /**
     * Monitor that validates and auto-corrects the CSV header.
     * It normalizes spacing, removes double-spaces, trims columns,
     * and rebuilds the CSV file with the corrected header.
     */
    @Around("execution(* ro.uaic.ossp.services.GradeService.uploadCsv(..)) && args(file, overwriteIfExists)")
    public Object validateHeader(ProceedingJoinPoint pjp, MultipartFile file, boolean overwriteIfExists) throws Throwable {

        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        String header = br.readLine();

        if (header == null) {
            throw new BadRequestException("CSV header missing");
        }

        // Auto-fix: trim and normalize spacing inside column names
        String normalizedHeader = Arrays.stream(header.split(","))
                .map(h -> h.trim().replaceAll(" +", " "))
                .reduce((a, b) -> a + "," + b)
                .orElse(header);

        // Rebuild the full CSV with the normalized header
        StringBuilder sb = new StringBuilder();
        sb.append(normalizedHeader).append("\n");

        // Append the remaining lines unchanged
        br.lines().forEach(line -> sb.append(line).append("\n"));
        br.close();

        // Create a runtime-safe MultipartFile (not using MockMultipartFile)
        MultipartFile fixedFile = new CustomMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                sb.toString().getBytes(StandardCharsets.UTF_8)
        );

        // Proceed with the corrected CSV file
        return pjp.proceed(new Object[]{fixedFile, overwriteIfExists});
    }
}

// java
package ro.uaic.ossp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ro.uaic.ossp.models.User;
import ro.uaic.ossp.models.enums.UserRole;
import ro.uaic.ossp.repositories.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@example.com";
        String adminPassword = "secret";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("Created test admin: " + adminEmail + " / " + adminPassword);
        } else {
            System.out.println("Admin already exists: " + adminEmail);
        }
    }
}
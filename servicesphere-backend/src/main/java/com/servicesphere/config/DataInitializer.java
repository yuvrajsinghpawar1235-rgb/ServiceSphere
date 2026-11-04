package com.servicesphere.config;

import com.servicesphere.entity.Role;
import com.servicesphere.entity.User;
import com.servicesphere.repository.RoleRepository;
import com.servicesphere.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // Seed Roles if empty
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("EMPLOYEE", "Standard user / requester"));
                roleRepository.save(new Role("AGENT", "Support agent handling incidents"));
                roleRepository.save(new Role("MANAGER", "Support lead / manager"));
                roleRepository.save(new Role("ASSET_MANAGER", "IT asset manager"));
                roleRepository.save(new Role("ADMIN", "System administrator"));
            }

            // Seed Demo Admin Account if empty
            if (userRepository.findByEmail("admin@servicesphere.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("System Administrator");
                admin.setEmail("admin@servicesphere.com");
                admin.setPasswordHash("pass123");
                admin.setRoleId(5L); // Admin role
                admin.setStatus("ACTIVE");
                userRepository.save(admin);
            }
        };
    }
}
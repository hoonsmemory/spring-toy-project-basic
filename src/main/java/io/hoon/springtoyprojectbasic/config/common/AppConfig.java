package io.hoon.springtoyprojectbasic.config.common;

import io.hoon.springtoyprojectbasic.repository.ResourceRepository;
import io.hoon.springtoyprojectbasic.service.security.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(ResourceRepository resourceRepository) {
        SecurityResourceService securityResourceService = new SecurityResourceService(resourceRepository);
        return securityResourceService;
    }
}

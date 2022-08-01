package io.hoon.springtoyprojectbasic.config.common;

import io.hoon.springtoyprojectbasic.repository.DenyIpRepository;
import io.hoon.springtoyprojectbasic.repository.ResourceRepository;
import io.hoon.springtoyprojectbasic.service.security.impl.SecurityResourceServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceServiceImpl securityResourceService(ResourceRepository resourceRepository, DenyIpRepository accessIpRepository) {
        SecurityResourceServiceImpl securityResourceServiceImpl = new SecurityResourceServiceImpl(resourceRepository, accessIpRepository);
        return securityResourceServiceImpl;
    }
}

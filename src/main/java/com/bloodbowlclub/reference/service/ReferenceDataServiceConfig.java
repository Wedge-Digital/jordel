package com.bloodbowlclub.reference.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class ReferenceDataServiceConfig {

    @Bean("referenceDataServiceForAPI")
    @Scope("singleton")
    public ReferenceDataService referenceDataService(ResourceLoader resourceLoader) {
        var service = new ReferenceDataService(resourceLoader);
        service.loadReferenceData();
        return service;
    }
}

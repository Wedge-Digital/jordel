package com.bloodbowlclub.team_building.ref_data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class RefDataServiceConfig {

    @Bean
    @Scope("singleton") // C'est le scope par défaut
    public ReferenceDataService referenceData(ResourceLoader resourceLoader) {
        // Ce code s'exécute une seule fois au démarrage
        var data = new ReferenceDataService(resourceLoader);
        data.loadReferenceData();      // Chargement depuis JSON/DB
        return data;
    }
}

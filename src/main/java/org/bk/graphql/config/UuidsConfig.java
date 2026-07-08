package org.bk.graphql.config;

import org.bk.graphql.util.Uuids;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UuidsConfig {
    @Bean
    public Uuids uuids() {
        return Uuids.systemUuid();
    }
}

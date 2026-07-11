package org.bk.books.config;

import org.bk.books.util.Uuids;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UuidsConfig {
    @Bean
    public Uuids uuids() {
        return Uuids.systemUuid();
    }
}

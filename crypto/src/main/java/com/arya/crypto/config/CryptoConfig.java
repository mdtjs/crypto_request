package com.arya.crypto.config;

import com.arya.crypto.filter.RequestDuplicateFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
@Configuration
public class CryptoConfig {

    @Bean
    public RequestDuplicateFilter requestDuplicateFilter() {
        return new RequestDuplicateFilter();
    }
}

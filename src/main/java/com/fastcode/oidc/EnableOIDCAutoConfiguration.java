package com.fastcode.oidc;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("com.fastcode.oidc")
@EntityScan(basePackages = "com.fastcode.oidc.domain.model")
@EnableJpaRepositories(basePackages = {"com.fastcode.oidc.domain.irepository"})
public class EnableOIDCAutoConfiguration {
}

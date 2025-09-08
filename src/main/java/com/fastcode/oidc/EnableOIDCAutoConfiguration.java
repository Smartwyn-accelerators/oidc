package com.fastcode.oidc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@ComponentScan("com.fastcode.oidc")
@EntityScan(basePackages = "com.fastcode.oidc.domain.model")
@EnableJpaRepositories(basePackages = {"com.fastcode.oidc.domain.irepository"})
public class EnableOIDCAutoConfiguration {
}

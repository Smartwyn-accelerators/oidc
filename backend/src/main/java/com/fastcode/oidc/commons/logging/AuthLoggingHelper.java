package com.fastcode.oidc.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthLoggingHelper {
    private Logger logger;

    public Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}

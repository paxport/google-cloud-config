package com.paxport.gcp.config.auth;

import com.paxport.gcp.config.GlobalProperty;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * Grab Secret from Datastore
 */
@Component
@DependsOn(value = "ObjectifyEntities")
public class AuthSecret implements InitializingBean {

    private String secret;

    public String getSecret() {
        return secret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        secret = GlobalProperty.fetch("authentication.secret");
    }
}

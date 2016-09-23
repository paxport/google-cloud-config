package com.paxport.gcp.config.global;


import com.paxport.gcp.config.ConfigTarget;
import com.paxport.gcp.config.auth.PaxportClaims;
import com.paxport.gcp.config.auth.UnauthorizedException;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Authorise reading/writing to agent config kind in datastore
 */
@Component
public class GlobalConfigService {

    public Optional<GlobalConfig> fetchConfig(String configKey, ConfigTarget target){
        return fetchConfig(configKey, PaxportClaims.boundPrincipal());
    }

    public Optional<GlobalConfig> fetchConfig(String configKey, PaxportClaims principal){
        authorise(principal);
        return GlobalConfig.fetch(configKey);
    }

    public void storeConfig(String configKey, String json) {
        storeConfig(configKey, json, PaxportClaims.boundPrincipal());
    }

    public void storeConfig(String configKey, String json, PaxportClaims principal) {
        authorise(principal);
        GlobalConfig.of(configKey,json).save();
    }

    private void authorise(PaxportClaims principal) {
        if ( principal == null || !principal.isSuperUser() ) {
            throw new UnauthorizedException("Only super users can read and write Global Properties");
        }
    }

    public void deleteConfig(String configKey, PaxportClaims principal) {
        authorise(principal);
        GlobalConfig.delete(configKey);
    }
}

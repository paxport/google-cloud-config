package com.paxport.gcp.config;


import com.paxport.gcp.config.auth.PaxportClaims;
import com.paxport.gcp.config.auth.UnauthorizedException;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Authorise reading/writing to global-props kind in datastore
 */
@Component
public class GlobalPropertyService {

    public Optional<String> fetchValue(String name, PaxportClaims principal){
        authorise(principal);
        return GlobalProperty.fetchValue(name);
    }

    public GlobalProperty storeValue(String name, String value,PaxportClaims principal) {
        authorise(principal);
        return GlobalProperty.of(name,value).save();
    }

    private void authorise(PaxportClaims principal) {
        if ( !principal.isSuperUser() ) {
            throw new UnauthorizedException("Only super users can read and write Global Properties");
        }
    }
}

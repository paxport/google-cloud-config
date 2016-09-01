package com.paxport.gcp.config.agent;


import com.paxport.gcp.config.ConfigTarget;
import com.paxport.gcp.config.auth.PaxportClaims;
import com.paxport.gcp.config.auth.UnauthorizedException;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Authorise reading/writing to agent config kind in datastore
 */
@Component
public class AgentConfigService {

    public Optional<AgentConfig> fetchAgentConfig(String agentId, String configKey, ConfigTarget target){
        return fetchAgentConfig(agentId, configKey, target, PaxportClaims.boundPrincipal());
    }

    public Optional<AgentConfig> fetchAgentConfig(String agentId, String configKey, ConfigTarget target, PaxportClaims principal){
        authoriseAgentRead(agentId,target,principal);
        return AgentConfig.fetch(agentId,configKey,target);
    }

    public void storeAgentConfig(String agentId, String configKey, ConfigTarget target, String json) {
        storeAgentConfig(agentId, configKey, target, json, PaxportClaims.boundPrincipal());
    }

    public void storeAgentConfig(String agentId, String configKey, ConfigTarget target, String json, PaxportClaims principal) {
        authoriseAgentWrite(agentId,target,principal);
        AgentConfig.of(agentId,configKey,target,json).save();
    }

    private void authoriseAgentRead(String agentId, ConfigTarget target, PaxportClaims principal) {
        if ( principal == null ) {
            throw new UnauthorizedException("No principal found");
        }
        if ( target == ConfigTarget.PRODUCTION && !principal.isProductionAllowed() ) {
            throw new UnauthorizedException("Not authorised for production");
        }
        if ( !principal.isSuperUser() ) {
            if ( !principal.isAgent() ) {
                throw new UnauthorizedException("Not authorised for any agent reads");
            }
            if ( !principal.getAgentId().equals( agentId) ) {
                throw new UnauthorizedException("Not authorised for reading " + agentId + " config");
            }
        }
    }

    private void authoriseAgentWrite(String agentId, ConfigTarget target, PaxportClaims principal) {
        authoriseAgentRead(agentId,target,principal);
        if ( !principal.isSuperUser() && !principal.isAdmin() ) {
            throw new UnauthorizedException("Not authorised for any writes");
        }
    }

}

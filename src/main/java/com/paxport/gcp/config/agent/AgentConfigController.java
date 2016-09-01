package com.paxport.gcp.config.agent;

import com.paxport.gcp.config.ConfigTarget;
import com.paxport.gcp.config.auth.AuthService;
import com.paxport.gcp.config.auth.PaxportClaims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/config/agent")
public class AgentConfigController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AgentConfigService agentConfigService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{agentId}/{configKey}/{target}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public HttpEntity fetchAgentConfig(@PathVariable String agentId,
                                       @PathVariable String configKey,
                                       @PathVariable ConfigTarget target,
                                       @RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        Optional<AgentConfig> agentConfig = agentConfigService.fetchAgentConfig(agentId,configKey,target,principal);
        if ( agentConfig.isPresent() ) {
            return ResponseEntity.ok(agentConfig.get().getJson());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{agentId}/{configKey}/{target}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public HttpEntity fetchAgentConfig(@PathVariable String agentId,
                                       @PathVariable String configKey,
                                       @PathVariable ConfigTarget target,
                                       @RequestBody String json,
                                       @RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        agentConfigService.storeAgentConfig(agentId,configKey,target,json,principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

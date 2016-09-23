package com.paxport.gcp.config.global;

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
@RequestMapping("/v1/config/global")
public class GlobalConfigController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GlobalConfigService configService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{configKey}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public HttpEntity fetchConfig(@PathVariable String configKey,@RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        Optional<GlobalConfig> config = configService.fetchConfig(configKey,principal);
        if ( config.isPresent() ) {
            return ResponseEntity.ok(config.get().getJson());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{configKey}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public HttpEntity storeAgentConfig(@PathVariable String configKey,
                                       @RequestBody String json,
                                       @RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        configService.storeConfig(configKey,json,principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

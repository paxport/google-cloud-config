package com.paxport.gcp.config.global;

import com.paxport.gcp.config.auth.AuthService;
import com.paxport.gcp.config.auth.PaxportClaims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
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
@RequestMapping("/v1/config/global/props")
@Profile("config-endpoints")
public class GlobalPropertyController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GlobalPropertyService globalPropertyService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/{name}",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public HttpEntity fetchGlobalPropertyValue(@PathVariable String name,
                                       @RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        Optional<String> value = globalPropertyService.fetchValue(name,principal);
        if ( value.isPresent() ) {
            return ResponseEntity.ok(value);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{name}",
            consumes = MediaType.TEXT_PLAIN_VALUE
    )
    public HttpEntity storeGlobalProperty(@PathVariable String name,
                                       @RequestBody String value,
                                       @RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        GlobalProperty prop = globalPropertyService.storeValue(name,value,principal);
        return ResponseEntity.accepted().body(prop);
    }
}

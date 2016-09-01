package com.paxport.gcp.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by ajchesney on 23/08/2016.
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<PaxportClaims> getPrincipalDetails(@RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        return ResponseEntity.ok(principal);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<String> createNewToken(@RequestBody PaxportClaims claims, @RequestHeader Map<String,String> headers) {
        PaxportClaims principal = authService.parseHeaders(headers);
        String token = authService.createNewToken(claims,principal);
        return ResponseEntity.ok(token);
    }

}

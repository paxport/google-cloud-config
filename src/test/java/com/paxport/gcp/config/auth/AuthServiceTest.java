package com.paxport.gcp.config.auth;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajchesney on 01/09/2016.
 */
public class AuthServiceTest {


    @Test
    public void testTokenGeneration() {

        AuthSecret secret = new AuthSecret("foo");
        AuthService service = new AuthService();
        service.setSecret(secret);

        PaxportClaims principal = PaxportClaims.of("admin",null,false,false,true);
        PaxportClaims claims = PaxportClaims.of("user","testagent",false,false,false);

        String token = service.createNewToken(claims, principal);

        Map<String,String> headers = new HashMap<>();
        headers.put("paxport-security-token",token);

        PaxportClaims test = service.parseHeaders(headers);

        Assert.assertEquals(claims,test);

    }

}

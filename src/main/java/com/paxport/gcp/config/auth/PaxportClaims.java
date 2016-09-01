package com.paxport.gcp.config.auth;

import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import io.jsonwebtoken.Claims;

@Value.Immutable
@Value.Style(
        get = {"is*", "get*"},
        depluralize = true,
        builderVisibility = Value.Style.BuilderVisibility.PACKAGE
)
@JsonSerialize(as = ImmutablePaxportClaims.class)
@JsonDeserialize(as = ImmutablePaxportClaims.class)
public abstract class PaxportClaims {

    private static final ThreadLocal<PaxportClaims> BOUND_PRINCIPAL = new ThreadLocal<>();

    private final static String USER_ID = "uid";
    private final static String AGENT_ID = "aid";
    private final static String PRODUCTION_ALLOWED = "pa";
    private final static String INTERNAL = "i";
    private final static String ADMIN = "ad";
    private final static String SUPER_USER = "su";

    public abstract String getUserId();

    @Nullable
    public abstract String getAgentId();

    @Value.Derived
    public boolean isAgent() {
        return getAgentId() != null;
    }

    public abstract boolean isAdmin();

    public abstract boolean isSuperUser();

    public abstract boolean isProductionAllowed();

    public abstract boolean isInternal();

    /**
     * Check these claims are valid to be created by the given principal
     * @param principal
     */
    void validateCreation (PaxportClaims principal) {
        if ( principal == null ) {
            throw new UnauthorizedException("No valid principal");
        }
        if (principal.isInternal()) {
            throw new UnauthorizedException("Principal is internal and cannot create tokens");
        }
        if ( !principal.isSuperUser() ) {
            if ( !isAdmin() ) {
                throw new UnauthorizedException("Principal is not authorized to create tokens");
            }
            else if ( !isAgent() ) {
                throw new UnauthorizedException("Only super users can create non agent tokens");
            }
            else if ( !getAgentId().equals(principal.getAgentId() ) ) {
                throw new UnauthorizedException("Principal is not authorized to create tokens for other agents");
            }
            else if ( isProductionAllowed() && !principal.isProductionAllowed() ) {
                throw new UnauthorizedException("Principal is not authorized to create production tokens");
            }
        }
    }

    public Map<String,Object> claimsMap() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put(USER_ID,getUserId());
        if ( isAgent() ){
            map.put(AGENT_ID,getAgentId());
        }
        if ( isProductionAllowed() ) {
            map.put(PRODUCTION_ALLOWED,true);
        }
        if ( isAdmin() ) {
            map.put(ADMIN,true);
        }
        if (isSuperUser()) {
            map.put(SUPER_USER,true);
        }
        if ( isInternal() ){
            map.put(INTERNAL,true);
        }
        return ImmutableMap.copyOf(map);
    }

    public static PaxportClaims of(Claims claims) {
        return ImmutablePaxportClaims.builder()
                .userId(claims.get(USER_ID,String.class))
                .agentId(claims.get(AGENT_ID,String.class))
                .productionAllowed(getBoolean(claims,PRODUCTION_ALLOWED))
                .internal(getBoolean(claims,INTERNAL))
                .admin(getBoolean(claims,ADMIN))
                .superUser(getBoolean(claims,SUPER_USER))
                .build();
    }

    private static boolean getBoolean(Claims claims, String key) {
        if ( claims.containsKey(key) ) {
            return claims.get(key, Boolean.class);
        }
        else {
            return false;
        }
    }

    public static PaxportClaims of (String userId,
                                    String agentId,
                                    boolean productionAllowed,
                                    boolean admin,
                                    boolean superUser ) {
        return ImmutablePaxportClaims.builder()
                .userId(userId)
                .agentId(agentId)
                .productionAllowed(productionAllowed)
                .internal(false)
                .admin(admin)
                .superUser(superUser)
                .build();
    }

    public static PaxportClaims internal (String userId, String agentId, boolean productionAllowed) {
        return ImmutablePaxportClaims.builder()
                .userId(userId)
                .agentId(agentId)
                .productionAllowed(productionAllowed)
                .internal(true)
                .admin(false)
                .superUser(false)
                .build();
    }

    public static void bindPrincipal(PaxportClaims principal){
        BOUND_PRINCIPAL.set(principal);
    }

    public static PaxportClaims boundPrincipal() {
        return BOUND_PRINCIPAL.get();
    }

    public static void unbindPrincipal(){
        BOUND_PRINCIPAL.remove();
    }
}

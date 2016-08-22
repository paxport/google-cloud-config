package com.paxport.gcp.config.auth;

import com.sun.istack.internal.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class StandardClaims {

    private final static String USER_ID = "userId";
    private final static String AGENT_ID = "agentId";
    private final static String TARGET = "target";
    private final static String INTERNAL = "internal";
    private final static String ADMIN = "admin";
    private final static String SUPER = "super";

    public abstract String getUserId();

    @Nullable
    public abstract String getAgentId();

    public abstract boolean isProduction();

    @Nullable
    public abstract boolean isInternal();

    @Nullable
    public abstract boolean isAdmin();

    @Nullable
    public abstract boolean isSuper();




}

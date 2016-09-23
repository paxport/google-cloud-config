package com.paxport.gcp.config.global;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.paxport.gcp.config.json.ConfigJsonUtils;
import com.paxport.storify.Storify;
import com.paxport.storify.annotation.Cache;
import com.paxport.storify.annotation.Entity;
import com.paxport.storify.annotation.Id;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable(builder = true)
@JsonSerialize(as = ImmutableGlobalConfig.class)
@JsonDeserialize(as = ImmutableGlobalConfig.class)
@Entity(kind="global-config", builderClass = ImmutableGlobalConfig.Builder.class)
@Cache(expirationSeconds = 300) // cache for 5 mins only
public abstract class GlobalConfig {

    @Id
    public abstract String getConfigKey();
    public abstract String getJson();

    public static GlobalConfig of(String configKey, Object obj){
        return ImmutableGlobalConfig.builder()
                .configKey(configKey)
                .json(ConfigJsonUtils.toJson(obj))
                .build();
    }

    public static GlobalConfig of(String configKey, String json){
        return ImmutableGlobalConfig.builder()
                .configKey(configKey)
                .json(json)
                .build();
    }

    public <E> E buildObject(Class<E> targetType){
        return ConfigJsonUtils.fromJson(getJson(),targetType);
    }

    public <E> E buildObject(TypeReference<E> targetType){
        return ConfigJsonUtils.fromJson(getJson(),targetType);
    }

    public GlobalConfig save() {
        Storify.sfy().put(this);
        return this;
    }

    public static Optional<GlobalConfig> fetch(String configKey) {
        return Storify.sfy().load(GlobalConfig.class,configKey);
    }

    public static <E> Optional<E> fetchObject( String configKey, Class<E> targetType) {
        Optional<GlobalConfig> agentConfig = Storify.sfy().load(GlobalConfig.class,configKey);
        if ( agentConfig.isPresent() ) {
            return Optional.of(agentConfig.get().buildObject(targetType));
        }
        else {
            return Optional.empty();
        }
    }
}

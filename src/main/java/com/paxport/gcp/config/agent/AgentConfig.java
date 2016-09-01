package com.paxport.gcp.config.agent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.paxport.gcp.config.ConfigTarget;
import com.paxport.gcp.config.json.ConfigJsonUtils;
import com.paxport.storify.Storify;
import com.paxport.storify.annotation.Entity;
import com.paxport.storify.annotation.Id;
import com.paxport.storify.annotation.IgnoreLoad;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable(builder = true)
@JsonSerialize(as = ImmutableAgentConfig.class)
@JsonDeserialize(as = ImmutableAgentConfig.class)
@Entity(name="agent-config", builderClass = ImmutableAgentConfig.Builder.class)
public abstract class AgentConfig {

    public abstract String getAgentId();
    public abstract String getConfigKey();
    public abstract ConfigTarget getTarget();// TEST or PRODUCTION
    public abstract String getJson();

    public static String key (String agentId, String configKey, ConfigTarget target) {
        return agentId + ":" + configKey + ":" + target;
    }

    @Id
    @IgnoreLoad
    public String getKey() {
        return key(getAgentId(),getConfigKey(),getTarget());
    }

    public static AgentConfig of(String agentId, String configKey, ConfigTarget target, Object obj){
        return ImmutableAgentConfig.builder()
                .agentId(agentId)
                .configKey(configKey)
                .target(target)
                .json(ConfigJsonUtils.toJson(obj))
                .build();
    }

    public static AgentConfig of(String agentId, String configKey, ConfigTarget target, String json){
        return ImmutableAgentConfig.builder()
                .agentId(agentId)
                .configKey(configKey)
                .target(target)
                .json(json)
                .build();
    }

    public <E> E buildObject(Class<E> targetType){
        return ConfigJsonUtils.fromJson(getJson(),targetType);
    }

    public AgentConfig save() {
        Storify.sfy().put(this);
        return this;
    }

    public static Optional<AgentConfig> fetch(String agentId, String configKey, ConfigTarget target) {
        return Storify.sfy().load(AgentConfig.class,key(agentId,configKey,target));
    }

    public static <E> Optional<E> fetchObject(String agentId, String configKey, ConfigTarget target, Class<E> targetType) {
        Optional<AgentConfig> agentConfig = Storify.sfy().load(AgentConfig.class,key(agentId,configKey,target));
        if ( agentConfig.isPresent() ) {
            return Optional.of(agentConfig.get().buildObject(targetType));
        }
        else {
            return Optional.empty();
        }
    }
}

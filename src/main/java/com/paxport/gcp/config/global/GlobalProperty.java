package com.paxport.gcp.config.global;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.paxport.storify.Storify;
import com.paxport.storify.annotation.Cache;
import com.paxport.storify.annotation.Entity;
import com.paxport.storify.annotation.Id;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Store and Retrieve simple global properties
 */
@Value.Immutable(builder = true)
@JsonSerialize(as = ImmutableGlobalProperty.class)
@JsonDeserialize(as = ImmutableGlobalProperty.class)
@Entity(kind="global-props", builderClass = ImmutableGlobalProperty.Builder.class)
@Cache
public abstract class GlobalProperty {

    @Id
    @Value.Parameter
    public abstract String getName();

    @Value.Parameter
    public abstract String getValue();

    public GlobalProperty save() {
        Storify.sfy().put(this);
        return this;
    }

    public static Optional<GlobalProperty> fetch(String name) {
        return Storify.sfy().load(GlobalProperty.class,name);
    }

    public static Optional<String> fetchValue(String name) {
        Optional<GlobalProperty> prop = fetch(name);
        if ( prop.isPresent() ) {
            return Optional.ofNullable(prop.get().getValue());
        }
        else {
            return Optional.empty();
        }
    }



    public static GlobalProperty of(String name, String value){
        return ImmutableGlobalProperty.of(name,value);
    }

    public static void delete(String name) {
        Storify.sfy().delete(GlobalProperty.class,name);
    }

}

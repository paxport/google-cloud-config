package com.paxport.gcp.config;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Store and Retrieve simple global properties
 */
@Entity
@Cache
public class GlobalProperty {

    @Id
    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public GlobalProperty setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public GlobalProperty setValue(String value) {
        this.value = value;
        return this;
    }

    public static String fetch(String name){
        Key<GlobalProperty> key = Key.create(GlobalProperty.class,name);
        return ObjectifyService.ofy().load().key(key).safe().getValue();
    }
}

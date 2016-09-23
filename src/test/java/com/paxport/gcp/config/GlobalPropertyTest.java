package com.paxport.gcp.config;

import com.paxport.gcp.config.global.GlobalProperty;

import org.junit.Assert;
import org.junit.Test;

public class GlobalPropertyTest {

    @Test
    public void testGlobalProperty() {

        GlobalProperty gp = GlobalProperty.of("test.global.prop","foo");
        gp.save();

        String value = GlobalProperty.fetchValue("test.global.prop").get();
        Assert.assertEquals("foo", value);

    }

}

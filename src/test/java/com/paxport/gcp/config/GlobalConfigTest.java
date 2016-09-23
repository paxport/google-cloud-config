package com.paxport.gcp.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.paxport.gcp.config.global.GlobalConfig;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfigTest {


    @Test
    public void testPutAndGet() {

        List<String> someConfig = new ArrayList<>();
        someConfig.add("foo");
        someConfig.add("bar");

        GlobalConfig.of("global-config-test-put-and-get",someConfig).save();

        List<String> result = GlobalConfig.fetch("global-config-test-put-and-get").get().buildObject(new TypeReference<List<String>>(){});
        Assert.assertEquals(someConfig,result);
    }

}

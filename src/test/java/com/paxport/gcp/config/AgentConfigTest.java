package com.paxport.gcp.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.paxport.gcp.config.agent.AgentConfig;
import com.paxport.gcp.config.global.GlobalConfig;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AgentConfigTest {


    @Test
    public void testPutAndGet() {

        List<String> someConfig = new ArrayList<>();
        someConfig.add("foo");
        someConfig.add("bar");

        AgentConfig.of("test-agent", "agent-config-test-put-and-get", ConfigTarget.TEST,someConfig).save();

        List<String> result = AgentConfig.fetch("test-agent", "agent-config-test-put-and-get", ConfigTarget.TEST)
                .get().buildObject(new TypeReference<List<String>>(){});
        Assert.assertEquals(someConfig,result);
    }

}

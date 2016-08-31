package com.paxport.gcp.config;

import com.google.common.collect.FluentIterable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.paxport"})
public class TestApplicationConfig extends SpringBootServletInitializer {

    /**
     * Need this to be able to run as a WAR
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestApplicationConfig.class);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(FluentIterable.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm());
        SpringApplication.run(TestApplicationConfig.class, args);
    }
}

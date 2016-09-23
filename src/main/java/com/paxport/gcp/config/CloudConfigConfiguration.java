package com.paxport.gcp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CloudConfigConfiguration {


    /**
     * This is required so that config keys with dots in will not be truncated
     * when they appear in urls
     */
    @Configuration
    protected static class AllResources extends WebMvcConfigurerAdapter {
        @Override
        public void configurePathMatch(PathMatchConfigurer matcher) {
            matcher.setUseRegisteredSuffixPatternMatch(true);
        }
    }

}

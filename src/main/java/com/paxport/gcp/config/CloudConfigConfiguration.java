package com.paxport.gcp.config;

import com.googlecode.objectify.ObjectifyFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudConfigConfiguration {


    /**
     * Configure Objectify Servlet Filter to clean up thread locals etc
     * @return
     */
    @Bean
    @Autowired
    public FilterRegistrationBean objectifyFilterRegistration() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new ObjectifyFilter());
        filterRegistrationBean.setOrder(1); // ordering in the filter chain
        return filterRegistrationBean;
    }
}

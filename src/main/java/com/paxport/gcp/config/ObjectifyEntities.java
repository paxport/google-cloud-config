package com.paxport.gcp.config;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

/**
 * Register all our entities
 */
@Component(value = "ObjectifyEntities")
public class ObjectifyEntities implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(ObjectifyEntities.class);

    @Value("${entity.scan.package:'com.paxport'}")
    private String scanPackage;

    @Override
    public void afterPropertiesSet() throws Exception {
        findAndRegisterEntities();
    }

    protected void findAndRegisterEntities() {
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents(scanPackage())) {
            registerEntity(beanDef);
        }
    }

    private void registerEntity(BeanDefinition beanDef) {
        try {
            logger.info("Found @Entity class: " + beanDef.getBeanClassName() );
            Class cls = Thread.currentThread().getContextClassLoader().loadClass(beanDef.getBeanClassName());
            registryEntityClass ( cls );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class " + beanDef.getBeanClassName(), e);
        }
    }

    private void registryEntityClass(Class cls) {
        ObjectifyService.register(cls);
    }

    protected String scanPackage() {
        return scanPackage;
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // Don't pull default filters (@Component, etc.):
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        return provider;
    }
}

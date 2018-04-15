package com.slesh.gallery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import java.io.IOException;

@ComponentScan({"com.slesh.gallery"})
@Import({MongoConfig.class, RepositoryRestMvcConfiguration.class})
public class AppConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertiesPropertySource() throws IOException {
        PropertySourcesPlaceholderConfigurer source = new PropertySourcesPlaceholderConfigurer();
        ResourcePropertySource propertySource = new ResourcePropertySource("classpath:application.properties");
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(propertySource);
        source.setPropertySources(propertySources);
        return source;
    }

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig(config -> {
            config.setBasePath("/api");
        });
    }

}

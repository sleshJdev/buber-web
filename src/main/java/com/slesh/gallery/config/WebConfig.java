package com.slesh.gallery.config;

import com.slesh.gallery.persistence.model.Ad;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.BasicLinkBuilder;

import java.util.Collections;

@Configuration
public class WebConfig {

    @Bean
    public ResourceProcessor<Resource<Ad>> personProcessor() {
        return new ResourceProcessor<Resource<Ad>>() {
            @Override
            public Resource<Ad> process(Resource<Ad> resource) {
                String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
                Link bannerLink = new Link(new UriTemplate(baseUri + "/api/ads/banner/{id}")
                    .with("id", TemplateVariable.VariableType.PATH_VARIABLE), "banner")
                    .expand(Collections.singletonMap("id", resource.getContent().getId()));
                resource.add(bannerLink);
                return resource;
            }
        };
    }


    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig(config -> {
            config.setBasePath("/api");
        });
    }
}

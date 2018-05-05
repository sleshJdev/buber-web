package com.slesh.gallery.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import java.io.File;
import java.nio.file.Paths;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        super.customizeRegistration(registration);
        String userHome = System.getProperty("java.io.tmpdir");
        File bannersDir = Paths.get(userHome, ".gallery/banners").toFile();
        if (!bannersDir.exists()) {
            if (!bannersDir.mkdirs()) {
                throw new RuntimeException(String.format("Could create %s directory", bannersDir));
            }
        }
        registration.setMultipartConfig(new MultipartConfigElement(userHome));
    }
}

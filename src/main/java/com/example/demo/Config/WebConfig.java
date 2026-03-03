package com.example.demo.Config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Apontando diretamente para o volume montado no Easypanel
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/opt/uploads/");
    }
}

package com.toomany.config;

import com.toomany.config.auth.LoginInterceptor;
import com.toomany.config.auth.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Value("${security.cors.host.front}")
    private String front;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(loginInterceptor)
                .excludePathPatterns("/api/auth/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins(front)
                .allowedMethods("*")
                .maxAge(1800);
    }
}

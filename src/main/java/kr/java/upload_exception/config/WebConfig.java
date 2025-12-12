package kr.java.upload_exception.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // @Value 필드 주입
    // org.springframework.beans.factory.annotation.Value
    @Value("${file.upload-dir}")
    private String uploadDir;

    // addResourceHandlers
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        WebMvcConfigurer.super.addResourceHandlers(registry);

        String absolutePath = Paths.get(uploadDir)
                .toAbsolutePath().normalize().toString();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath + "/")
                .setCachePeriod(3600); // 60 * 60 : 1시간.
    }
}

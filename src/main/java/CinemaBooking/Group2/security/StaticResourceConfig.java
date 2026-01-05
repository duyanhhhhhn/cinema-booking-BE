package CinemaBooking.Group2.security;

import java.nio.file.Paths;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
	@Value("${app.upload.dir}")
	private String uploadDir;
	
	@Value("${app.upload.public-prefix:/media}")
	private String publicPrefix;
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        if (!location.endsWith("/")) location += "/";

        registry.addResourceHandler(publicPrefix + "/**")
                .addResourceLocations(location);
    }
}

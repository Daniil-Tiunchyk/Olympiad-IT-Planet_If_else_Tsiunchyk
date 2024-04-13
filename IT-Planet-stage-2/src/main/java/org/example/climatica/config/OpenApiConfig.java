package org.example.climatica.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components())
                .info(new Info()
                        .title("Climatica System API")
                        .description("This API serves the Tests Management System API.")
                        .version("1.0.0")
                        .contact(
                                new Contact()
                                        .name("Daniil Tsiunchyk (www.linkedin.com/in/daniil-tsiunchyk)")
                        )
                );
    }
}

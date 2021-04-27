package mediscreen.doctor.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {

        ApiInfo info = new ApiInfo(
                "Doctor",
                "Module for medical notes management written during project 9 of the AXA software academy.",
                "1.0.0",
                null,
                "Philippe GEY",
                null,
                null
        );

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(info)
                .select()
                .apis(RequestHandlerSelectors.basePackage("mediscreen.doctor.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}

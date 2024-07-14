package com.excitel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenAPIConfig {

    @Value("${broadband.openapi.dev-url}")
    private String devUrl;
    @Value("${broadband.openapi.prod-url}")
    private String prodUrl;
    @Value("${broadband.openapi.contact.email}")
    private String contactEmail;
    @Value("${broadband.openapi.contact.url}")
    private String contactUrl;
    @Value("${broadband.openapi.contact.name}")
    private String contactName;

    @Value("${broadband.license.name}")
    private String licenseName;
    @Value("${broadband.license.url}")
    private String licenseUrl;

    /**
     * Creates and configures the OpenAPI document.
     *
     * @return An instance of OpenAPI representing the API documentation
     */

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in local Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production development environment");

        // Define contact information
        Contact contact = new Contact();
        contact.setEmail(contactEmail);
        contact.setName(contactName);
        contact.setUrl(contactUrl);

        // Define license information
        License mitLicense = new License().name(licenseName).url(licenseUrl);
        // Define API information
        Info info = new Info()
                .title("Excitel Broadband Management API's")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to operate Excitel Broadband Subscription Microservice.")
                .termsOfService("Excitel Terms & Condition")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}

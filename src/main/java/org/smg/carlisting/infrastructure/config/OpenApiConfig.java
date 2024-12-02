package org.smg.carlisting.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI documentation.
 * <p>
 * This class configures the OpenAPI specification for the Car Listing application.
 * It provides details such as the title, description, version, and contact information
 * to be displayed in the generated API documentation.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI object with application-specific information.
     * <p>
     * This method sets up the OpenAPI information for the application, including the
     * title, description, version, and contact details of the API. This information
     * is displayed in the Swagger UI and other OpenAPI tools.
     * </p>
     *
     * @return The configured OpenAPI object.
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("Car Listings Microservice")
                .description("Car listings on the online marketplace")
                .version("v0.0.1")
                .contact(getContactDetails()));
    }

    /**
     * Provides contact details to be included in the OpenAPI specification.
     * <p>
     * This method returns the contact information of the primary maintainer or contact
     * person for the API. It includes the name and email address.
     * </p>
     *
     * @return A Contact object containing the name and email of the contact person.
     */
    private Contact getContactDetails() {
        return new Contact().name("Aleksandar Berdovic")
                .email("aleksandar.berdovic@gmail.com");
    }
}
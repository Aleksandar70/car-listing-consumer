package org.smg.carlisting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CarListingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarListingApplication.class, args);
    }

}

package org.example.springstarterproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringStarterProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringStarterProjectApplication.class, args);
    }

}

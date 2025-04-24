package cumulocity.microservice.service.request.mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;

@MicroserviceApplication
@EnableScheduling
public class App {
    public static void main (String[] args) {
        SpringApplication.run(App.class, args);
    }
}
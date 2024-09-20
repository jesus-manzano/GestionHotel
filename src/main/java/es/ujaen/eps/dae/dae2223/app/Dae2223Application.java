package es.ujaen.eps.dae.dae2223.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={
        "es.ujaen.eps.dae.dae2223.servicios",
        "es.ujaen.eps.dae.dae2223.repositorios",
        "es.ujaen.eps.dae.dae2223.rest",
        "es.ujaen.eps.dae.dae2223.seguridad"
})
@EntityScan(basePackages = "es.ujaen.eps.dae.dae2223.entidades")
@EnableScheduling
public class Dae2223Application {

    public static void main(String[] args) {
        SpringApplication.run(Dae2223Application.class, args);
    }

}

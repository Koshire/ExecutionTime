package pl.akulov.executiontime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExecutionTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExecutionTimeApplication.class, args);
    }

}

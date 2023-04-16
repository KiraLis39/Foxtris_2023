package ru.foxtris;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@Slf4j
@SpringBootApplication
public class FoxtrisApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(FoxtrisApp.class).headless(false).run(args);
    }
}

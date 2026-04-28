package com.tomato;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tomato.mapper")
public class TomatoTimerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TomatoTimerApplication.class, args);
    }
}

package com.sunzy.vulfocus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sunzy.vulfocus.mapper")
public class VulfocusApplication {

    public static void main(String[] args) {
        SpringApplication.run(VulfocusApplication.class, args);
    }

}

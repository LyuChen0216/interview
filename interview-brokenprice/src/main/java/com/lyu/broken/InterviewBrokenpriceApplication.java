package com.lyu.broken;

import com.lyu.broken.controller.BrokenPriceController;
import com.lyu.broken.service.BrokenPriceService;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.lyu.broken.mapper")
@SpringBootApplication
public class InterviewBrokenpriceApplication {

    public static void main(String[] args) {

        SpringApplication.run(InterviewBrokenpriceApplication.class, args);


    }

}

package com.servicenow.exam;

import com.servicenow.exam.service.impl.LineParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceNowExamApplication implements CommandLineRunner {

    @Autowired
    private LineParserService lineParserService;

    @Override
    public void run(String... args) throws Exception {
        lineParserService.parse();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceNowExamApplication.class, args);
    }

}

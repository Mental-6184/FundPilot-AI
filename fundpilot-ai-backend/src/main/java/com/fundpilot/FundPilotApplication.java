package com.fundpilot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * FundPilot AI 启动类
 * 基金智能分析平台
 */
@SpringBootApplication
@MapperScan("com.fundpilot.mapper")
@EnableCaching
@EnableAsync
@EnableScheduling
public class FundPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundPilotApplication.class, args);
        System.out.println("============================================");
        System.out.println("   FundPilot AI Backend Started!            ");
        System.out.println("   http://localhost:8080/doc.html            ");
        System.out.println("============================================");
    }
}

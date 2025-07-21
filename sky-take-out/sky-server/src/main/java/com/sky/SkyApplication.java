package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @EnableCaching // 开启缓存注解功能 - temporarily disabled
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
// @EnableCaching // 开启缓存注解功能 - temporarily disabled
@EnableTransactionManagement //开启注解方式的事务管理
@EnableScheduling // 开启任务调度
@Slf4j
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}

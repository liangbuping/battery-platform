package com.battery.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 电池安全管理平台启动类
 */
@SpringBootApplication
@MapperScan("com.battery.platform.mapper")
public class BatteryPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatteryPlatformApplication.class, args);
        System.out.println("========================================");
        System.out.println("  电池安全管理平台后端服务启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}

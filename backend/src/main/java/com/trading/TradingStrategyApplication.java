package com.trading;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.trading.mapper")
public class TradingStrategyApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingStrategyApplication.class, args);
    }
}

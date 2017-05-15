package com.zzm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync               //（异步回调）让@Async注解能够生效,不能加在静态方法上
@EnableScheduling          // 开启Scheduling 注解
@ServletComponentScan
@SpringBootApplication
public class RocketmqStarterTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketmqStarterTestApplication.class, args);
	}
}

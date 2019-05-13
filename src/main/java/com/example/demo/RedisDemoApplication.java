package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RedisDemoApplication {
 
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(RedisDemoApplication.class, args);
		
		RedisListCpt bean = run.getBean(RedisListCpt.class);
		
//		bean.testMtrheadGet();
		bean.testThreadGet();
		
		run.close();
	}

}

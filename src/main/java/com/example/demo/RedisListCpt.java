package com.example.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisListCpt {

	@Autowired
    RedisTemplate<String, String> template;
	
	String key = "redis_list_test";
	
	public String get() {
		ListOperations<String, String> ops = template.opsForList();
		return ops.leftPop(key);
	}
	
	public void rpush(String value) {
		ListOperations<String, String> ops = template.opsForList();
		ops.rightPush(key, value);
	}
	
	// 多线程获取List中值，可以实现队列的
	public void testMtrheadGet() {
		for (int i = 0; i < 500; i++) {
			this.rpush("value"+i);
		}
		
		ExecutorService pool = Executors.newFixedThreadPool(20);
		
		AtomicInteger total = new AtomicInteger(0);
		for (int i = 0; i < 10; i++) {
			pool.submit(()->{
				while(true) {
					String v = this.get();
					System.out.println(Thread.currentThread().getName() +": "+v + " total: "+ total.getAndIncrement());
					if(v == null) {
						break;
					}
				}
			});
		}
	}
}

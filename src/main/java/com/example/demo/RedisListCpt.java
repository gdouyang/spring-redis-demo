package com.example.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
		
		ExecutorService pool = Executors.newFixedThreadPool(8);
		
		AtomicInteger total = new AtomicInteger(0);
		for (int i = 0; i < 10; i++) {
			pool.execute(()->{
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
	// 测试多线程中对一个key进行删除与获取
	public void testThreadGet() {
		ValueOperations<String, String> ops = template.opsForValue();
		ExecutorService pool = Executors.newFixedThreadPool(2);
		String key = "threadDelete";
		pool.execute(()->{
			ops.set(key, "0");
			for (int i = 1; i < 600; i++) {
				template.delete(key);
				System.out.println(Thread.currentThread().getName() +" delete");
				ops.set(key, ""+i);
				System.out.println(Thread.currentThread().getName() +" set value: "+ i);
			}
		});
		
		pool.execute(()->{
			for (int i = 0; i < 500; i++) {
				System.out.println(Thread.currentThread().getName() +" get value: "+ ops.get(key));
			}
		});
	}
	
}

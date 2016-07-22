package com.sourcecode.spring.job;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class CronJobExample {
    
    
    @Scheduled(cron="*/5 * * * * ?")
    public void demoService(){
        System.out.println("Method executed at every 5 seconds. Current time is :: "+ new Date());
    }
}

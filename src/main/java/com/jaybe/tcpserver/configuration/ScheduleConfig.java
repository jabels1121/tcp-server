package com.jaybe.tcpserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleConfig {

    private static final String TASK_EXECUTOR_NAME_PREFIX = "tcp-server-exec-";
    private static final String TASK_SCHEDULER_NAME_PREFIX = "tcp-server-sched-";

    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = POOL_SIZE * 2;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor result = new ThreadPoolTaskExecutor();
        result.setCorePoolSize(POOL_SIZE);
        result.setMaxPoolSize(MAX_POOL_SIZE);
        result.setThreadNamePrefix(TASK_EXECUTOR_NAME_PREFIX);
        return result;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler result = new ThreadPoolTaskScheduler();
        result.setPoolSize(MAX_POOL_SIZE);
        result.setThreadNamePrefix(TASK_SCHEDULER_NAME_PREFIX);
        return result;
    }
}

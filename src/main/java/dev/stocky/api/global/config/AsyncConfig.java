package dev.stocky.api.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // 1. 기본적으로 대기하고 있는 일꾼 수 (평소)
    executor.setCorePoolSize(5);

    // 2. 일이 몰리면 최대 늘릴 수 있는 일꾼 수 (최대)
    executor.setMaxPoolSize(20);

    // 3. 일꾼들이 다 바쁠 때 대기표 뽑고 기다리는 줄의 길이
    executor.setQueueCapacity(50);

    // 스레드 이름 접두사 (로그 볼 때 편함: Async-1, Async-2 ...)
    executor.setThreadNamePrefix("Async-");

    executor.initialize();
    return executor;
  }

}

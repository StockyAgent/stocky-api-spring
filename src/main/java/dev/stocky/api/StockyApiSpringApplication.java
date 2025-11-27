package dev.stocky.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StockyApiSpringApplication {

  public static void main(String[] args) {

    System.setProperty("aws.region", "ap-northeast-2"); // todo: 환경변수로 변경

    SpringApplication.run(StockyApiSpringApplication.class, args);
  }

}

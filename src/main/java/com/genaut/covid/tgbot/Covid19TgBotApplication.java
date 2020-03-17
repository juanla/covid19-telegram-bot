package com.genaut.covid.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class Covid19TgBotApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(Covid19TgBotApplication.class, args);
	}

}

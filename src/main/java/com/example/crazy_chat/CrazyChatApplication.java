package com.example.crazy_chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CrazyChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(CrazyChatApplication.class, args);
	}

}

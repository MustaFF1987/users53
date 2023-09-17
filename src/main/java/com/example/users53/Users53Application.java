package com.example.users53;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Users53Application {

	// @DataJpaTest - только загружать часть Spring ответственную за
	// работу с базой данных

	// @WebMvcTest - загружается только часть связанная с контроллерами

	// @SpringBootTest - загружает все компоненты приложения

	public static void main(String[] args) {
		SpringApplication.run(Users53Application.class, args);
	}

}

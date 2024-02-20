package com.csetutorials.ssj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsjApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SsjApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Main.main(args);
	}
}

package com.expenses.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.expenses.*")
public class MyExpenseManagerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyExpenseManagerApiApplication.class, args);
	}

}

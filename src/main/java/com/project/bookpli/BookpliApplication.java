package com.project.bookpli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BookpliApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookpliApplication.class, args);
	}

}

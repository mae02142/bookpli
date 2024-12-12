package com.project.bookpli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableJpaRepositories("com.project.bookpli")
public class BookpliApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookpliApplication.class, args);
	}

}

package com.example.UserBase;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DemoApplication {

	@Autowired
	private DataSource dataSource;

	@RequestMapping("/hello")
	public String helloDockerCompose() {
		Integer idleConnection = new HikariDataSourcePoolMetadata((HikariDataSource) dataSource).getIdle();
		return "Hello Docker Compose! Idle connection to database is " + idleConnection;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

package com.example.SearchEngineHibernate;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication()
public class SearchEngineHibernateApplication {

	public static void main(String[] args) throws SQLException, IOException {
		SpringApplication.run(SearchEngineHibernateApplication.class, args);
	}

}

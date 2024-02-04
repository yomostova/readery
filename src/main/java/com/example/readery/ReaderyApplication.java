package com.example.readery;
import com.example.readery.utils.InitService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WebSecurityConfig.class)

public class ReaderyApplication {
	@Autowired
	InitService initService;

	public static void main(String[] args) {
		SpringApplication.run(ReaderyApplication.class, args);
	}

	@PostConstruct
	public void start(){
		//to insert data into database at initial setup,
		// change initDB.enabled=true in application.properties
		initService.setUp();
	}
}

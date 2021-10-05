package com.worldingroup.worldin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WorldinApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorldinApplication.class, args);
	}
	
	@RequestMapping("/")
	public String hello()   
	{  
		return "<h1>Hello from Worldin, we're up and running...</h1>";  
	}

}


package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ProjetoLactalisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoLactalisApplication.class, args);
				// System.out.println(new BCryptPasswordEncoder().encode("123456"));

		
	}

}

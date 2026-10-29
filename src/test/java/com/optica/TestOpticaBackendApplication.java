package com.optica;

import org.springframework.boot.SpringApplication;

public class TestOpticaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(OpticaBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

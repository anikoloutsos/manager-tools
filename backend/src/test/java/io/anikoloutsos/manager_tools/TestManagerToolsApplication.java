package io.anikoloutsos.manager_tools;

import org.springframework.boot.SpringApplication;

public class TestManagerToolsApplication {

	public static void main(String[] args) {
		SpringApplication.from(ManagerToolsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

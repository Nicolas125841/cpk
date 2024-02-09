package com.ristn.cpk;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "my-app")
public class CpkApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(CpkApplication.class, args);
	}

}

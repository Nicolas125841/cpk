package com.ristn.cpk.server.services;

import java.io.IOException;

import com.ristn.cpk.server.data.AdminData;
import lombok.extern.java.Log;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@Log
public class ServiceConfig {
	@Bean
	public AdminData adminData() {
		return new AdminData();
	}
	@Bean
	public AdminService adminService(AdminData adminData, UserDetailsManager userDetailsManager) throws IOException {
		return new AdminService(adminData, userDetailsManager);
	}
	@Bean
	public PackageService packageService(AdminService adminService) {
		return new PackageService(adminService);
	}
}

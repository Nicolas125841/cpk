package com.ristn.cpk.server.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.ristn.cpk.server.data.AdminData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;

@Getter
@Setter
@Log
public class AdminService {

	private final String CONFIG_FILE = "server-config.yaml";
	private final String DEFAULT =
        """
        username: admin
        password: password
        serverUrl: C:\\Users\\Nicolas\\Documents\\FileServer
        contactEmail: admin@email.com
        """;

	private Yaml yaml;

	AdminData dataHook;
	UserDetailsManager admin;

	public AdminService(AdminData adminData, @Autowired UserDetailsManager userDetailsManager) throws IOException {
		admin = userDetailsManager;
		dataHook = adminData;

		LoaderOptions adminOptions = new LoaderOptions();
		TagInspector tagInspector = tag -> tag.getClassName().equals(AdminData.class.getName());

		adminOptions.setTagInspector(tagInspector);

		yaml = new Yaml(new Constructor(AdminData.class, adminOptions));

		loadConfig();
	}

	public void setCredentials(AdminData dataBean) {
		dataHook.setPassword(dataBean.getPassword());

		try {
			storeConfig();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setUrl(AdminData dataBean) {
		dataHook.setServerUrl(dataBean.getServerUrl());

		try {
			storeConfig();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setEmail(AdminData dataBean) {
		dataHook.setContactEmail(dataBean.getContactEmail());

		try {
			storeConfig();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private void loadConfig() throws FileNotFoundException {
		log.info("Loading...");

		dataHook.load(yaml.load(new FileInputStream(CONFIG_FILE)));

		admin.deleteUser("admin");
		admin.createUser(User.withUsername("admin")
		                     .password("{noop}" + dataHook.getPassword())
		                     .roles("ADMIN")
		                     .build());
	}

	private void restoreConfig() throws IOException {
		try(FileWriter fw = new FileWriter(CONFIG_FILE)) {
			fw.write(DEFAULT);
			fw.close();

			loadConfig();
		}
	}

	private void storeConfig() throws IOException {
		try(FileWriter fw = new FileWriter(CONFIG_FILE)) {
			fw.write(yaml.dump(dataHook));
		}

		loadConfig();
	}
}

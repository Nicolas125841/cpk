package com.ristn.cpk.server.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminData {
	private volatile String password;
	private volatile String serverUrl;
	private volatile String contactEmail;

	public AdminData() {}

	public AdminData(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void load(AdminData other) {
		this.password = other.password;
		this.serverUrl = other.serverUrl;
		this.contactEmail = other.contactEmail;
	}
}

package com.ristn.cpk.server.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class CPackage {
	private String packageName;

	@Override
	public int hashCode() {
		return packageName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof CPackage) {
			return packageName.equals(((CPackage) other).packageName);
		} else {
			return false;
		}
	}
}

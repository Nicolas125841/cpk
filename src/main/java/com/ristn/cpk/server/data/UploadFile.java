package com.ristn.cpk.server.data;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadFile {
	private String      name;
	private InputStream stream;
}

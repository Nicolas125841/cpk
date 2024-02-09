package com.ristn.cpk.server.data;

import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UPackage {
	private String name;
	private MultiFileMemoryBuffer files;
}

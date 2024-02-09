package com.ristn.cpk.server.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Stream;

import com.ristn.cpk.server.data.AdminData;
import com.ristn.cpk.server.data.CPackage;
import com.ristn.cpk.server.data.UploadFile;
import com.vaadin.flow.router.NotFoundException;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import org.springframework.util.FileSystemUtils;

@Getter
@Log
public class PackageService {
	private final AdminService adminService;
	private Path baseUrl;
	private final HashSet<CPackage> packages = new HashSet<>();

	public PackageService(AdminService adminService) {
		this.adminService = adminService;

		reloadPackages();
	}
	public boolean uploadPackage(String name, Stream<UploadFile> files) {
		Path packagePath = baseUrl.resolve(name);

		try {
			Files.createDirectory(packagePath);
			ZipArchiveOutputStream zipOutput =
					new ZipArchiveOutputStream(packagePath.resolve(name + ".zip").toFile());

			boolean result =  files.allMatch(file -> {
				try {
					Path filePath = packagePath.resolve(file.getName());

					if(Files.copy(file.getStream(), filePath) >= 0) {
						zipOutput.putArchiveEntry(zipOutput.createArchiveEntry(filePath, file.getName()));
						Files.copy(filePath, zipOutput);
						zipOutput.closeArchiveEntry();

						return true;
					}

					return false;
				}
				catch (Exception e) {
					log.info(e.getMessage());

					return false;
				}
			});

			zipOutput.close();
			reloadPackages();

			return result;
		}
		catch (Exception exception) {
			return false;
		}
	}

	public boolean removePackage(String name) {
		CPackage tmp = new CPackage(name);

		try {
			if (packages.contains(tmp) && FileSystemUtils.deleteRecursively(baseUrl.resolve(name))) {
				packages.remove(tmp);

				reloadPackages();

				return true;
			}
			else {
				return false;
			}
		} catch (IOException exception) {
			log.info(exception.getMessage());

			return false;
		}
	}

	public void downloadPackage(String name, OutputStream target) {
		CPackage tmp = new CPackage(name);

		try {
			if (packages.contains(tmp) && baseUrl.resolve(name).toFile().exists()) {
				Files.copy(baseUrl.resolve(name).resolve(name + ".zip"), target);
			}
		} catch (IOException exception) {
			log.info(exception.getMessage());
		}
	}

	public InputStream getPackageDetails(String name) {
		CPackage tmp = new CPackage(name);

		try {
			if (packages.contains(tmp) && baseUrl.resolve(name).resolve(name + ".md").toFile().exists()) {
				return new FileInputStream(baseUrl.resolve(name).resolve(name + ".md").toFile());
			} else {
				throw new NotFoundException();
			}
		} catch (IOException exception) {
			log.info(exception.getMessage());

			throw new RuntimeException(exception);
		}
	}

	public boolean reindexPackages(String newUrl) {
		Path newPath = Path.of(newUrl);

		if(newPath.toFile().exists() && newPath.toFile().isDirectory()) {
			baseUrl = newPath;
			adminService.setUrl(new AdminData(newPath.toFile().getAbsolutePath()));

			reloadPackages();

			return true;
		}

		return false;
	}

	public void reloadPackages() {
		baseUrl = Path.of(adminService.getDataHook().getServerUrl());
		packages.clear();
		packages.addAll(Arrays.stream(Objects.requireNonNull(baseUrl.toFile().listFiles()))
		                      .map(File::getName)
		                      .map(CPackage::new).toList());
	}
}

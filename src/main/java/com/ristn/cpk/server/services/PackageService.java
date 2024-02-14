package com.ristn.cpk.server.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.ristn.cpk.server.data.AdminData;
import com.ristn.cpk.server.data.CPackage;
import com.ristn.cpk.server.data.UploadFile;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.vaadin.flow.router.NotFoundException;
import lombok.Getter;
import lombok.extern.java.Log;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import org.springframework.util.FileSystemUtils;

@Getter
@Log
public class PackageService {
	public static final MediaType MEDIA_TYPE_MARKDOWN
			= MediaType.parse("text/markdown; charset=utf-8");
	public static final MediaType MEDIA_TYPE_ZIP
			= MediaType.parse("application/zip");
	public static final MediaType MEDIA_TYPE_JSON
			= MediaType.parse("application/json");
	public static final String ENDPOINT_ADD = "add";
	public static final String ENDPOINT_REMOVE = "remove";
	public static final String ENDPOINT_PACKAGES = "packages";
	private final OkHttpClient okHttpClient = new OkHttpClient();
	private final Moshi moshi = new Moshi.Builder().build();
	private final JsonAdapter<List<String>> packageListAdapter = moshi.adapter(new TypeToken<List<String>>() {}.getType());
	private final AdminService adminService;
	private URI baseUrl;
	private final HashSet<CPackage> packages = new HashSet<>();

	public PackageService(AdminService adminService) {
		this.adminService = adminService;

		baseUrl = URI.create(adminService.getDataHook().getServerUrl());

		reloadPackages();
	}
	public boolean uploadPackage(String name, Stream<UploadFile> files) {
		try {
			Path launchpad = Files.createTempDirectory(name);
			ZipArchiveOutputStream zipOutput =
					new ZipArchiveOutputStream(launchpad.resolve(name + ".zip").toFile());

			boolean result =  files.allMatch(file -> {
				try {
					Path filePath = launchpad.resolve(file.getName());

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

			if(result) {
				RequestBody requestBody = new MultipartBody.Builder()
						.setType(MultipartBody.FORM)
						.addFormDataPart("name", name)
						.addFormDataPart("files", name + ".md",
								RequestBody.create(launchpad.resolve(name + ".md").toFile(), MEDIA_TYPE_MARKDOWN))
						.addFormDataPart("files", name + ".zip",
								RequestBody.create(launchpad.resolve(name + ".zip").toFile(), MEDIA_TYPE_ZIP))
						.build();

				Request request = new Request.Builder()
						.url(baseUrl.resolve(ENDPOINT_ADD).toString())
						.post(requestBody)
						.build();

				try (Response response = okHttpClient.newCall(request).execute()) {
					if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

					log.info(Objects.requireNonNull(response.body()).string());
				} catch (Exception error) {
					log.info(error.getMessage());

					result = false;
				}
			}

			reloadPackages();

			FileSystemUtils.deleteRecursively(launchpad);

			return result;
		}
		catch (Exception exception) {
			return false;
		}
	}

	public boolean removePackage(String name) {
		CPackage tmp = new CPackage(name);

		try {
			if (packages.contains(tmp)) {
				RequestBody formBody = new FormBody.Builder()
						.add("name", name)
						.build();

				Request request = new Request.Builder()
						.url(baseUrl.resolve(ENDPOINT_REMOVE).toString())
						.delete(formBody)
						.build();

				try (Response response = okHttpClient.newCall(request).execute()) {
					if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

					packages.remove(tmp);

					reloadPackages();

					return true;
				}
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
			if (packages.contains(tmp)) {
				Request request = new Request.Builder()
						.url(baseUrl.resolve(ENDPOINT_PACKAGES + "/" + name + "/" + name + ".zip").toString())
						.get()
						.build();

				try (Response response = okHttpClient.newCall(request).execute()) {
					if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

					Streams.copy(Objects.requireNonNull(response.body())
					                    .byteStream(), target, true);
				}
			} else {
				throw new NotFoundException();
			}
		} catch (IOException exception) {
			log.info(exception.getMessage());

			throw new RuntimeException(exception);
		}
	}

	public String getPackageDetails(String name) {
		CPackage tmp = new CPackage(name);

		try {
			if (packages.contains(tmp)) {
				Request request = new Request.Builder()
						.url(baseUrl.resolve(ENDPOINT_PACKAGES + "/" + name + "/" + name + ".md").toString())
						.get()
						.build();

				try (Response response = okHttpClient.newCall(request).execute()) {
					if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

					return Objects.requireNonNull(response.body()).string();
				}
			} else {
				throw new NotFoundException();
			}
		} catch (IOException exception) {
			log.info(exception.getMessage());

			throw new RuntimeException(exception);
		}
	}

	public boolean reindexPackages(String newUrl) {
		URI newUri = URI.create(newUrl);

		Request request = new Request.Builder()
				.url(newUri.resolve(ENDPOINT_PACKAGES).toString())
				.get()
				.build();

		try (Response response = okHttpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			adminService.setUrl(new AdminData(newUri.toString()));
			baseUrl = newUri;

			reloadPackages();

			return true;
		} catch (Exception error) {
			log.info(error.getMessage());
		}

		return false;
	}

	private void reloadPackages() {
		packages.clear();

		Request request = new Request.Builder()
				.url(baseUrl.resolve(ENDPOINT_PACKAGES).toString())
				.get()
				.build();

		try (Response response = okHttpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			packages.addAll(
					Objects.requireNonNull(
							packageListAdapter
									.fromJson(Objects.requireNonNull(response.body()).source()))
			       .stream().map(CPackage::new).toList());

			log.info(packages.toString());
		} catch (Exception error) {
			log.info(error.getMessage());
		}
	}
}

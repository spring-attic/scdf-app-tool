/*
 * Copyright 2018 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.springframework.cloud.dataflow.apptool.command;

import static org.springframework.cloud.dataflow.apptool.AppResource.WILDCARD;
import static org.springframework.cloud.dataflow.apptool.Utils.fatal;
import static org.springframework.cloud.dataflow.apptool.Utils.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.dataflow.apptool.AppInfo;
import org.springframework.cloud.dataflow.apptool.AppResource;
import org.springframework.cloud.dataflow.apptool.ComponentTypeValidator;
import org.springframework.cloud.dataflow.apptool.CustomAppResource;
import org.springframework.cloud.dataflow.apptool.Utils;
import org.springframework.core.io.UrlResource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * @author David Turanski
 *
 * This class implements the 'repo' shell commands for managing download artifacts.
 **/
@ShellComponent
public class Repo {

	private static final String REPO_LIST = "repo list";
	private static final String REPO_LS = "repo ls";
	private static final String REPO_RM = "repo rm";
	private static final String REPO_ADD = "repo add";
	private static final String REPO_CLEAN = "repo clean";

	@Autowired
	private AppInfo appInfo;

	@Value("${local.repo.directory}")
	private String repoDirectory;

	private AppResourceDownloader appResourceDownloader;

	@ShellMethod(value = "List local repository.", key = { REPO_LIST, REPO_LS })
	public void list() {
		appInfo.findAll().forEach(Utils::message);
	}

	@ShellMethod(value = "Clean local repository.", key = REPO_CLEAN)
	public void clean() {
		try {
			Files.list(Paths.get(repoDirectory)).filter(Files::isRegularFile).forEach(path -> {
				try {
					Files.delete(path);
				}
				catch (IOException e) {
					message(e.getMessage());
				}
			});
			appInfo.clean();
		}
		catch (IOException e) {
			fatal(e.getMessage());
		}
	}

	@ShellMethod(value = "Remove entries from local repository.", key = REPO_RM)
	public void rm(@ShellOption({ "-n", "--name" }) String name,
		@ShellOption(value = { "-t", "--type" }, defaultValue = WILDCARD) String type) {

		if (!ensureSupportedAppType(type)) {
			return;
		}

		List<String> entries = appInfo.findByNameAndType(name, type);

		if (CollectionUtils.isEmpty(entries)) {
			message(String.format("No entries found for name %s and type %s", name, type));
			return;
		}

		entries.forEach(f -> {
			try {
				Path path = Paths.get(repoDirectory, f);
				message(String.format("rm %s", path.getFileName()));
				Files.delete(path);
				appInfo.remove(path.getFileName().toString());
			}
			catch (IOException e) {
				message(e.getMessage());
			}
		});
	}

	@ShellMethod(value = "Add to local repository from a URL.", key = REPO_ADD)
	public void add(@ShellOption({ "-n", "--name" }) String name,
		@ShellOption(value = { "-t", "--type" }) String type,
		@ShellOption(value = {"--url"} , help = "The resource URL") String url,
		@ShellOption(value= {"--metadata"}, defaultValue= "", help = "The metadata URL(optional)") String metadataUrl) {

		if (!ComponentTypeValidator.isValidAppType(type)) {
			return;
		}

		CustomAppResource resource = new CustomAppResource(type, name, url);
		if (StringUtils.hasText(metadataUrl)) {
			resource.setMetadataUrl(metadataUrl);
		}

		appResourceDownloader.accept(resource);
		AppResource metadataResource = resource.getMetadataResource();
		if (metadataResource != null) {
			appResourceDownloader.accept(metadataResource);
		}
	}

	@PostConstruct
	public void init() {
		this.appResourceDownloader = new AppResourceDownloader(repoDirectory, appInfo);
	}

	private boolean ensureSupportedAppType(String type) {
		if (type.equals(WILDCARD)) {
			return true;
		}
		return ComponentTypeValidator.isValidAppType(type);
	}
}

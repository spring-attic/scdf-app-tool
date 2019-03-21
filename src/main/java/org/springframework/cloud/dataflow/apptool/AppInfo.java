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

package org.springframework.cloud.dataflow.apptool;

import static org.springframework.cloud.dataflow.apptool.Utils.fatal;
import static org.springframework.cloud.dataflow.apptool.Utils.loadPropertiesFile;
import static org.springframework.cloud.dataflow.apptool.Utils.message;

import org.springframework.util.PatternMatchUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This persists the current app repository state, including the name and app type for each artifact. It is used to
 * search the repository, list contents, and generate the import file from the app repo server.
 *
 * @author David Turanski
 **/
public class AppInfo {

	private final File appInfoFile;

	public void clean() {

		try {
			if (Files.exists(appInfoFile.toPath())) {
				Files.delete(appInfoFile.toPath());
			}
		}
		catch (IOException e) {
			message("Error:" + e.getMessage());
		}
	}

	public AppInfo(String localRepoDirectory) {
		this.appInfoFile = Paths.get(localRepoDirectory, "app-info.properties").toFile();
	}

	public void add(AppResource appResource) {
		try {
			if (!this.appInfoFile.exists()) {
				appInfoFile.createNewFile();
			}

			Map<String, String> appInfo = loadPropertiesFile(appInfoFile);

			appInfo.putIfAbsent(appResource.getKey(), appResource.getFilename());

			update(appInfo);

		}
		catch (Exception e) {
			fatal(e.getMessage());
		}
	}

	public void remove(String path) {

		try {
			Map<String, String> appInfo = loadPropertiesFile(appInfoFile);
			appInfo.entrySet().removeIf(e -> e.getValue().equals(path));
			update(appInfo);
		}
		catch (Exception e) {
			fatal(e.getMessage());
		}
	}

	public List<String> findAll() {
		List<String> result = new ArrayList<>();
		if (appInfoFile.exists()) {
			try {
				Map<String, String> appInfo = loadPropertiesFile(appInfoFile);
				result = appInfo.entrySet()
					.stream()
					.map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
					.sorted()
					.collect(Collectors.toList());
			}
			catch (Exception e) {
				fatal(e.getMessage());
			}
		}
		return result;
	}

	public Map<String, String> findAllAsMap() {
		Map<String, String> result = Collections.EMPTY_MAP;
		if (appInfoFile.exists()) {
			try {
				result = loadPropertiesFile(appInfoFile);
			}
			catch (Exception e) {
				fatal(e.getMessage());
			}
		}
		return result;
	}

	public List<String> findByNameAndType(String name, String type) {
		List<String> result = new ArrayList<>();
		if (appInfoFile.exists()) {
			try {
				Map<String, String> appInfo = loadPropertiesFile(appInfoFile);
				result = appInfo.keySet()
					.stream()
					.filter(k -> matches(type, name, k))
					.map(appInfo::get)
					.collect(Collectors.toList());

			}
			catch (Exception e) {
				fatal(e.getMessage());
			}
		}
		return result;
	}

	public boolean matches(String type, String name, String key) {
		String[] tokens = key.split("\\.");
		return PatternMatchUtils.simpleMatch(type, tokens[0]) && PatternMatchUtils.simpleMatch(name, tokens[1]);
	}

	private void update(Map<String, String> properties) throws Exception {
		PrintWriter writer = new PrintWriter(appInfoFile.getPath(), "UTF-8");
		new TreeMap<>(properties).forEach((k, v) -> writer.println(String.format("%s=%s", k, v)));
		writer.close();
	}

}

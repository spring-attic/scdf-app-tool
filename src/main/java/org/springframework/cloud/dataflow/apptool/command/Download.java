/*
 * Copyright 2018 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.springframework.cloud.dataflow.apptool.command;

import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.dataflow.apptool.AppInfo;
import org.springframework.cloud.dataflow.apptool.AppResource;
import org.springframework.cloud.dataflow.apptool.BinderResolver;
import org.springframework.cloud.dataflow.apptool.ComponentTypeValidator;
import org.springframework.cloud.dataflow.apptool.event.BinderUpdateEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import static org.springframework.cloud.dataflow.apptool.AppResource.WILDCARD;
import static org.springframework.cloud.dataflow.apptool.Utils.fatal;
import static org.springframework.cloud.dataflow.apptool.Utils.loadPropertiesFile;

/**
 * This class implements the shell commands related to downloading artifacts listed the config properties files.
 *
 * @author David Turanski
 **/
@ShellComponent
public class Download implements ApplicationListener<BinderUpdateEvent> {

	private static final String GET_STREAM_APPS = "get stream-apps";
	private static final String GET_TASK_APPS = "get task-apps";
	private static final String LIST_TASK_APPS = "list task-apps";
	private static final String LIST_STREAM_APPS = "list stream-apps";

	private final static String CONFIG_DIR = "config";

	@Value("${local.repo.directory}")
	private String repoDirectory;

	@Value("${maven.repo.url}")
	private String mavenRepoUrl;

	private Consumer<AppResource> appResourceConsumer;

	@Value("${binder:}")

	private String binder;

	@Autowired
	private AppInfo appInfo;

	public Download() {
	}

	public Download(Consumer<AppResource> appResourceConsumer) {
		this.appResourceConsumer = appResourceConsumer;
	}

	@ShellMethod(value = "Download stream app jars from maven.", key = { GET_STREAM_APPS })
	@ShellMethodAvailability("downloadStreamAvailability")
	public void downloadStreamApps(@ShellOption(value = { "-n", "--name" }, defaultValue = WILDCARD) String name,
		@ShellOption(value = { "-t", "--type" }, defaultValue = WILDCARD) String type) {

		if (!ensureSupportedComponentType(type)) {
			return;
		}

		try {
			loadStreamApps().map(e -> new AppResource(e.getKey(), e.getValue(), mavenRepoUrl))
				.filter(a -> a.matches(name, type))
				.forEach(a -> appResourceConsumer.accept(a));
		}
		catch (Exception e) {
			fatal(e.getMessage());
		}
	}

	@ShellMethod(value = "Download task app jars from maven or given URL.", key = { GET_TASK_APPS })
	public void downloadTaskApps(@ShellOption(value = { "-n", "--name" }, defaultValue = WILDCARD) String name) {

		try {
			loadTaskApps().map(e -> new AppResource(e.getKey(), e.getValue(), mavenRepoUrl))
				.filter(a -> a.matches(name, WILDCARD))
				.forEach(a -> appResourceConsumer.accept(a));
		}
		catch (Exception e) {
			fatal(e.getMessage());
		}
	}

	@ShellMethod(value = "List stream apps available for download.", key = LIST_STREAM_APPS)
	@ShellMethodAvailability("downloadStreamAvailability")
	public void listStreamApps() {
		try {
			loadStreamApps().map(e -> new AppResource(e.getKey(), e.getValue(), mavenRepoUrl))
				.filter(a -> !"metadata".equals(a.getClassifier()))
				.map(a -> String.format("%s:%s", a.getComponentType(), a.getName()))
				.sorted()
				.forEach(System.out::println);
		}
		catch (Exception e) {
			fatal(e.getMessage());
		}
	}

	@ShellMethod(value = "List task apps available for download.", key = LIST_TASK_APPS)
	public void listTaskApps() {
		try {
			loadTaskApps().map(e -> new AppResource(e.getKey(), e.getValue(), mavenRepoUrl))
				.filter(a -> !"metadata".equals(a.getClassifier()))
				.map(a -> String.format("%s", a.getName()))
				.sorted()
				.forEach(System.out::println);
		}
		catch (Exception e) {
			fatal(e.getMessage());
		}
	}

	public Availability downloadStreamAvailability() {
		return StringUtils.isEmpty(binder) ?
			Availability.unavailable("binder is not defined") :
			Availability.available();
	}

	private boolean ensureSupportedComponentType(String type) {
		if (type.equals(WILDCARD)) {
			return true;
		}
		return ComponentTypeValidator.isValidStreamAppType(type);
	}

	private Stream<Map.Entry<String, String>> loadStreamApps() throws Exception {
		return loadPropertiesFile(
			Paths.get(CONFIG_DIR, String.format("%s-stream-apps.properties", binder)).toFile()).entrySet().stream();
	}

	private Stream<Map.Entry<String, String>> loadTaskApps() throws Exception {
		return loadPropertiesFile(Paths.get(CONFIG_DIR, "task-apps.properties").toFile()).entrySet().stream();
	}

	@Override
	public void onApplicationEvent(BinderUpdateEvent binderUpdateEvent) {
		this.binder = binderUpdateEvent.getBinder();
	}

	@PostConstruct
	public void init() {
		this.appResourceConsumer = new AppResourceDownloader(repoDirectory, appInfo);
		if (StringUtils.hasText(this.binder)) {
			this.binder = BinderResolver.resolveBinder(binder);
			if (!StringUtils.hasText(binder)) {
				System.exit(1);
			}
		}
	}

}

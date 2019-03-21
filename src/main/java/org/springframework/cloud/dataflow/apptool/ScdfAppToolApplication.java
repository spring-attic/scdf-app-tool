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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author David Turanski
 */

@SpringBootApplication
public class ScdfAppToolApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder().web(WebApplicationType.NONE)
			.bannerMode(Banner.Mode.OFF)
			.sources(ScdfAppToolApplication.class)
			.run(args);
	}

	@Bean
	AppInfo appInfo(@Value("${local.repo.directory}") String localRepoDirectory) {
		return new AppInfo(localRepoDirectory);
	}
}

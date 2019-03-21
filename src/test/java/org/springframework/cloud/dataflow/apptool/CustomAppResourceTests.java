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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;

import org.springframework.core.io.UrlResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Turanski
 **/
public class CustomAppResourceTests {
	@Test
	public void defaultMetadata() {

		String path =
			"https://github.com/dturanski/spring-cloud-stream-binaries/blob/master/binaries/python-local-processor-rabbit"
				+ "-1.2.1.BUILD-SNAPSHOT.jar?raw=true";

		CustomAppResource resource = new CustomAppResource("processor", "python-local-processor-rabbit", path);

		URL metadataURL = resource.getMetadataURL();

		assertThat(metadataURL.toString()).isEqualTo(
			"https://github.com/dturanski/spring-cloud-stream-binaries/blob/master/binaries/python-local-processor-rabbit"
				+ "-1.2.1.BUILD-SNAPSHOT-metadata.jar?raw=true");
	}

	@Test
	public void localFile() throws IOException {

		File localjar = Paths.get("src/test/resources","my-local-app-0.0.1-SNAPSHOT.jar").toFile();

		CustomAppResource resource = new CustomAppResource("processor", "my-local-app",
			"file://" + localjar.getAbsolutePath());

		URL url = resource.getUrl();
		UrlResource urlResource = new UrlResource(url);
		assertThat(urlResource.getFile().exists()).isTrue();

		URL metadataUrl = resource.getMetadataURL();
		UrlResource metadataUrlResource = new UrlResource(metadataUrl);
		assertThat(metadataUrlResource.getFile().exists()).isTrue();

	}
}

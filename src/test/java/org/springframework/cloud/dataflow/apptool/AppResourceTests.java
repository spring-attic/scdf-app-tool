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

package org.springframework.cloud.dataflow.apptool;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;

/**
 * @author David Turanski
 **/
public class AppResourceTests {

	private String mavenRepoUrl = "https://my.repo";
	private String jar = "maven://org.springframework.cloud.stream.app:cassandra-sink-rabbit:1.3.1.RELEASE";
	private String metadata = "maven://org.springframework.cloud.stream"
		+ ".app:cassandra-sink-rabbit:jar:metadata:1.3.1.RELEASE";

	@Test
	public void jarResource() {

		AppResource appResource = new AppResource("sink.cassandra", jar,mavenRepoUrl);

		assertThat(appResource.getUrl().toString()).isEqualTo("https://my.repo/org/springframework/cloud/stream"
			+ "/app/cassandra-sink-rabbit/1.3.1.RELEASE/cassandra-sink-rabbit-1.3.1.RELEASE.jar");

		assertThat(appResource.getComponentType()).isEqualTo("sink");
		assertThat(appResource.getName()).isEqualTo("cassandra");
		assertThat(appResource.getFilename()).isEqualTo("cassandra-sink-rabbit-1.3.1.RELEASE.jar");
	}

	@Test
	public void metadataResource() {

		AppResource appResource = new AppResource("sink.cassandra.metadata", metadata,mavenRepoUrl);

		assertThat(appResource.getUrl().toString()).isEqualTo("https://my.repo/org/springframework/cloud/stream"
			+ "/app/cassandra-sink-rabbit/1.3.1.RELEASE/cassandra-sink-rabbit-1.3.1.RELEASE-metadata.jar");

		assertThat(appResource.getComponentType()).isEqualTo("sink");
		assertThat(appResource.getName()).isEqualTo("cassandra");
		assertThat(appResource.getClassifier()).isEqualTo("metadata");
		assertThat(appResource.getFilename()).isEqualTo("cassandra-sink-rabbit-1.3.1.RELEASE-metadata.jar");
	}

	@Test
	public void customUrl() {
		AppResource appResource = new AppResource("source.my-test","https://github"
			+ ".com/dturanski/spring-cloud-stream-binaries/blob/master/binaries/gemfire-cq-source-rabbit-v1.1.jar?raw"
			+ "=true",null);

		assertThat(appResource.getUrl().toString()).isEqualTo("https://github"
		+ ".com/dturanski/spring-cloud-stream-binaries/blob/master/binaries/gemfire-cq-source-rabbit-v1.1.jar?raw"
			+ "=true");

		assertThat(appResource.getFilename()).isEqualTo("gemfire-cq-source-rabbit-v1.1.jar");

	}

	@Test
	public void wildcardMatches() {
		String key = AppResource.WILDCARD;
		String type = AppResource.WILDCARD;
		AppResource jarResource = new AppResource("my.source", jar, mavenRepoUrl);
		AppResource metadataResource = new AppResource("my.source.metadata", metadata, mavenRepoUrl);
		assertThat(jarResource.matches(key, type)).isTrue();
		assertThat(metadataResource.matches(key,type)).isTrue();
	}

	@Test
	public void prefixMatches() {

		String key = "ca" + AppResource.WILDCARD;
		String type = "sink";
		AppResource jarResource = new AppResource("sink.cassandra", jar,mavenRepoUrl);
		AppResource metadataResource = new AppResource("sink.cassandra.metadata", metadata, mavenRepoUrl);
		assertThat(jarResource.matches(key, type)).isTrue();
		assertThat(metadataResource.matches(key,type)).isTrue();
	}

	@Test
	public void wrongTypeDoesNotMatch() {

		String key = "ca" + AppResource.WILDCARD;
		String type = "source";
		AppResource jarResource = new AppResource("sink.cassandra", jar, mavenRepoUrl);
		AppResource metadataResource = new AppResource("sink.cassandra.metadata", metadata, mavenRepoUrl);
		assertThat(jarResource.matches(key, type)).isFalse();
		assertThat(metadataResource.matches(key,type)).isFalse();
	}

	@Test
	public void postFixMatches() {

		String key = "*andra";
		String type = "sink";
		AppResource jarResource = new AppResource("sink.cassandra", jar, mavenRepoUrl);
		AppResource metadataResource = new AppResource("sink.cassandra.metadata", metadata, mavenRepoUrl);
		assertThat(jarResource.matches(key, type)).isTrue();
		assertThat(metadataResource.matches(key,type)).isTrue();
	}

	@Test
	public void infixMatches() {
		String key = "c*an*ra";
		String type = "sink";
		AppResource jarResource = new AppResource("sink.cassandra", jar, mavenRepoUrl);
		AppResource metadataResource = new AppResource("sink.cassandra.metadata", metadata, mavenRepoUrl);
		assertThat(jarResource.matches(key, type)).isTrue();
		assertThat(metadataResource.matches(key,type)).isTrue();
	}

}

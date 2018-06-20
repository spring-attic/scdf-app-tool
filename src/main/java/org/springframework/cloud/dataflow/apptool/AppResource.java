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

import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an application resource, i.e., the information contained in each entry in a app properties file.
 *
 * @author David Turanski
 **/

public class AppResource {
	public static final String WILDCARD = "*";

	private final String componentType;
	private final URL url;
	private final String name;
	private final String classifier;
	private final String mavenRepoUrl;
	private final String filename;

	/**
	 * @param key          '<name>:<type>:?metadata'
	 * @param value        the URL of the artifact, typically using the 'maven://' schema but may be an actual URL.
	 * @param mavenRepoUrl The base URL of the external maven repository containing the artifacts. Used to generate
	 *                     the resource actual URL
	 */
	public AppResource(String key, String value, String mavenRepoUrl) {
		Assert.hasText(key, "key must contain text");
		Assert.hasText(value, "value must contain text");
		String[] tokens = key.split("\\.");
		Assert.isTrue(tokens.length == 2 || tokens.length == 3, String.format("Invalid property key [%s]", key));
		this.mavenRepoUrl = mavenRepoUrl;
		this.componentType = tokens[0];
		this.name = tokens[1];
		this.classifier = tokens.length == 3 ? tokens[2] : null;
		this.url = convertUrl(value);
		this.filename = parseFileName(this.url);
	}

	private String parseFileName(URL url) {
		String[] toks = url.getPath().split("/");
		return toks[toks.length - 1];
	}

	protected URL convertUrl(String value) {
		try {
			if (value.startsWith("maven://")) {
				return new URL(parseMavenResource(value));
			}
			return new URL(value);
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

	}

	public String getComponentType() {
		return componentType;
	}

	public URL getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getClassifier() {
		return classifier;
	}

	public String getFilename() {
		return filename;
	}

	public String getKey() {
		return StringUtils.hasText(classifier) ?
			String.join(".", componentType, name, classifier) :
			String.join(".", componentType, name);
	}

	public String parseMavenResource(String coordinates) {
		coordinates = coordinates.replaceFirst("maven://", "");
		Assert.hasText(coordinates, "coordinates are required");
		Pattern p = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?:([^: ]+)");
		Matcher m = p.matcher(coordinates);
		Assert.isTrue(m.matches(), "Bad artifact coordinates " + coordinates
			+ ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>");
		String groupId = m.group(1);
		String artifactId = m.group(2);
		String extension = StringUtils.hasLength(m.group(4)) ? m.group(4) : "jar";
		String classifier = StringUtils.hasLength(m.group(6)) ? m.group(6) : "";
		String version = m.group(7);

		if (StringUtils.isEmpty(classifier)) {
			return String.format("%s/%s-%s.%s",
				String.join("/", mavenRepoUrl, groupId.replaceAll("\\.", "/"), artifactId, version), artifactId,
				version, extension);
		}
		return String.format("%s/%s-%s-%s.%s",
			String.join("/", mavenRepoUrl, groupId.replaceAll("\\.", "/"), artifactId, version), artifactId, version,
			classifier, extension);

	}

	public boolean matches(String key, String type) {
		return PatternMatchUtils.simpleMatch(key, this.name) && PatternMatchUtils.simpleMatch(type, this.componentType);
	}
}

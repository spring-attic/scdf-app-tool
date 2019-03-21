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

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.core.io.UrlResource;

/**
 * Represents an custom app resource that is given by a URL and does not have an entry in any of the *-apps.properties
 * files. Useful for locally registering locally built applications.
 *
 * @author David Turanski
 **/
public class CustomAppResource extends AppResource {
	private URL metadataUrl;

	public CustomAppResource(String componentType, String name, String url) {
		super(String.format("%s.%s", componentType, name), url, null);
	}

	public void setMetadataUrl(String metadataUrl) {
		try {
			this.metadataUrl = new URL(metadataUrl);
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public URL getMetadataURL() {
		return this.metadataUrl == null ? deriveMetadataURL() : this.metadataUrl;
	}

	public AppResource getMetadataResource() {
		if (new UrlResource(getMetadataURL()).exists()) {
			return new AppResource(getKey() + ".metadata", getMetadataURL().toString(), null);
		}
		return null;
	}

	private URL deriveMetadataURL() {
		String urlStr = getUrl().toString();
		String fileName = getUrl().getFile();
		String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
		URL metadataURL;
		try {
			metadataURL = new URL(
				urlStr.replaceAll(nameWithoutExtension, String.format("%s-metadata", nameWithoutExtension)));
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return metadataURL;
	}
}

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

/**
 * @author David Turanski
 **/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.springframework.cloud.dataflow.apptool.AppInfo;
import org.springframework.cloud.dataflow.apptool.AppResource;

import static org.springframework.cloud.dataflow.apptool.Utils.fatal;
import static org.springframework.cloud.dataflow.apptool.Utils.message;

/**
 *
 */
class AppResourceDownloader implements Consumer<AppResource> {

	private final String directory;

	private final AppInfo appInfo;

	AppResourceDownloader(String directory, AppInfo appInfo) {
		ensureWritableDirectory(directory);
		this.directory = directory;
		this.appInfo = appInfo;
	}

	@Override
	public void accept(AppResource appResource) {
		URL website = null;
		try {

			System.out.println(String.format("downloading %s...", appResource.getUrl()));
			downloadAppResource(appResource, directory);
			appInfo.add(appResource);

		}
		catch (Exception e) {
			message(e.getMessage());
		}
	}

	private void downloadAppResource(AppResource appResource, String directory) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(appResource.getUrl().openStream());
		FileOutputStream fos = new FileOutputStream(Paths.get(directory, appResource.getFilename()).toString());
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	private File ensureWritableDirectory(String directory) {
		Path path = Paths.get(directory);

		File outputDirectory = path.toFile();

		if (!Files.exists(path)) {
			try {
				outputDirectory = Files.createDirectory(path).toFile();

			}

			catch (IOException e) {
				fatal(e.getMessage());
			}
		}

		if (!outputDirectory.isDirectory()) {
			outputDirectory.delete();
			fatal(String.format("%s is not a directory", path.toString()));
		}

		if (!Files.isWritable(path)) {
			outputDirectory.delete();
			fatal(String.format("%s is not writable", path.toString()));
		}

		return outputDirectory;
	}

}
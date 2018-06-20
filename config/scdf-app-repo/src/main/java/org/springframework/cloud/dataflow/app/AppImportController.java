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

package org.springframework.cloud.dataflow.app;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

/**
 * @author David Turanski
 **/

@RestController
public class AppImportController {

	@GetMapping(value = "/import")
	public void downloadAppImport(HttpServletRequest request, HttpServletResponse response) {

		Resource appInfo = new ClassPathResource("/static/app-info.properties");

		if (appInfo.exists()) {
			String baseUrl = buildBaseUrl(request);
			StringBuilder contents = new StringBuilder();
			try {

				Properties properties = new Properties();
				properties.load(appInfo.getInputStream());

				properties.entrySet().forEach(e -> {
					contents.append(String.format("%s=%s/%s\n",e.getKey(), baseUrl, e.getValue()));
				});

				InputStream appImport = new ByteArrayInputStream(contents.toString().getBytes());

			response.setContentType("text/plain");
			response.addHeader("Content-Disposition", "attachment; filename=app-import.properties");

				IOUtils.copy(appImport, response.getOutputStream());
				response.getOutputStream().flush();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private String buildBaseUrl(HttpServletRequest request) {
		String url = String.format("%s://%s", request.getScheme(), request.getServerName());
		if (request.getServerPort() != 80) {
			url = url.concat(String.format(":%d",request.getServerPort()));
		}
		return url;
	}
}

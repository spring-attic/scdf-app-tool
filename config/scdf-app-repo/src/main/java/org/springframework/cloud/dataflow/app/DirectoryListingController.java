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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Turanski
 **/
@Controller
public class DirectoryListingController {

	@GetMapping(value = "/repo")
	public String list(Model model) {
		try {
			Resource[] jars = (new PathMatchingResourcePatternResolver(getClass().getClassLoader())).getResources(
				"classpath:static/*.jar");
			model.addAttribute("files",
				Stream.of(jars).map(Resource::getFilename).sorted().collect(Collectors.toList()));

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return "repo";
	}
}

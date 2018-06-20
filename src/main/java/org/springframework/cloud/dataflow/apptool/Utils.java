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

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * General Utilities.
 *
 * @author David Turanski
 **/
public abstract class Utils {

	public static void fatal(String message) {
		message(message);
		System.exit(1);
	}

	public static void message(String message) {
		System.out.println(message);
	}

	public static Map<String, String> loadPropertiesFile(File propertiesFile) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		return new HashMap(properties);
	}

}

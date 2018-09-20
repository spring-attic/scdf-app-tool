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

import static org.springframework.cloud.dataflow.apptool.Utils.message;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Utility for validating Stream component types, and app types
 *
 * @author David Turanski
 **/
public abstract class ComponentTypeValidator {
	private final static List<String> SUPPORTED_STREAM_APP_TYPES = Arrays.asList("source", "processor", "sink");
	private final static List<String> SUPPORTED_APP_TYPES = Arrays.asList("source", "processor", "sink","task","app");

	public static boolean isValidStreamAppType(String type) {
		if (!SUPPORTED_STREAM_APP_TYPES.contains(type)) {
			message("Supported stream component types are " + StringUtils.collectionToDelimitedString(
				SUPPORTED_STREAM_APP_TYPES, ","));
			return false;
		}
		return true;
	}

	public static boolean isValidAppType(String type) {
		if (!SUPPORTED_APP_TYPES.contains(type) ) {
			message("Supported stream component types are " + StringUtils.collectionToDelimitedString(
				SUPPORTED_APP_TYPES, ","));
			return false;
		}
		return true;
	}

}

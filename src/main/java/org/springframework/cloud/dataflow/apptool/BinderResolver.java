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

import static org.springframework.cloud.dataflow.apptool.Utils.message;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 *
 * Utility for validating the binder name, and resolving aliases, e.g., "kafka" -> "kafka-10"
 *
 * @author David Turanski
 **/
public abstract class BinderResolver {

	final static List<String> SUPPORTED_BINDERS = Arrays.asList("kafka", "rabbit");

	public static String resolveBinder(String binder) {
		String b = binder.toLowerCase();
		if (!SUPPORTED_BINDERS.contains(b)) {
			message(
				"Supported binders are " + StringUtils.collectionToDelimitedString(SUPPORTED_BINDERS, ","));
			return null;
		}

		return b;
	}
}

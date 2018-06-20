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

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.dataflow.apptool.event.BinderUpdateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * A {@link PromptProvider} indicating the current binder context.
 *
 * @author David Turanski
 **/
@Component
public class Prompt implements PromptProvider {

	@Value("${binder:}")
	private String binder;

	@Override
	public AttributedString getPrompt() {
		if (StringUtils.hasText(binder)) {
			return new AttributedString(binder + ":>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
		}
		else {
			return new AttributedString("binder-undefined:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
		}
	}

	@EventListener
	public void handle(BinderUpdateEvent event) {
		this.binder = event.getBinder();
	}

	@PostConstruct
	public void initBinder() {
		if (StringUtils.hasText(binder)) {
			binder = BinderResolver.resolveBinder(binder);
			if (!StringUtils.hasText(binder)) {
				System.exit(1);
			}
		}
	}
}
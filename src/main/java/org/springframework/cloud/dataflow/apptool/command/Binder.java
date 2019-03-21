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

package org.springframework.cloud.dataflow.apptool.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.dataflow.apptool.BinderResolver;
import org.springframework.cloud.dataflow.apptool.event.BinderUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * This class implements the 'binder' shell command for updating the binder name.
 *
 * @author David Turanski
 **/
@ShellComponent
public class Binder {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@ShellMethod(value = "Set the Binder to use for stream apps.", key = "binder")
	public void setBinder(String name) {
		applicationEventPublisher.publishEvent(new BinderUpdateEvent(this, BinderResolver.resolveBinder(name)));
	}

}

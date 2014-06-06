/*
 * Copyright (C) 2011 Ahmed Yehia (ahmed.yehia.m@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lightcouch;

/**
 * Represents CouchDB response as a result of a save, update or delete requests.
 * @author Ahmed Yehia
 *
 */
public class Response {
	private String id;
	private String rev;

	public String getId() {
		return id;
	}

	public String getRev() {
		return rev;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	@Override
	public String toString() {
		return "Response [id=" + id + ", rev=" + rev + "]";
	}
}

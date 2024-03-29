/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.ats.generator.objects.mouse;

import org.openqa.selenium.Keys;

import com.ats.generator.objects.MouseDirectionData;

public class MouseKey extends Mouse {
	
	private Keys key;

	public MouseKey(String type, Keys key) {
		super(type);
		setKey(key);
	}

	public MouseKey(String type, Keys key, MouseDirectionData hpos, MouseDirectionData vpos) {
		super(type, hpos, vpos);
		setKey(key);
	}

	public Keys getKey() {
		return key;
	}

	public void setKey(Keys key) {
		this.key = key;
	}

}

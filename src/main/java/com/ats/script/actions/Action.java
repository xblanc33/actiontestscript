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

package com.ats.script.actions;

import com.ats.executor.ActionStatus;
import com.ats.executor.ActionTestScript;
import com.ats.executor.channels.Channel;
import com.ats.script.Script;

public class Action {

	protected Script script;
	protected int line;
	protected boolean disabled = false;
	protected ActionStatus status;

	public Action(){}

	public Action(Script script){
		this.script = script;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	// Code Generator
	//---------------------------------------------------------------------------------------------------------------------------------

	public StringBuilder getJavaCode(){
		
		StringBuilder codeBuilder = new StringBuilder(ActionTestScript.JAVA_EXECUTE_FUNCTION_NAME);
		codeBuilder.append("(")
		.append(getLine())
		.append(",")
		.append("new ")
		.append(this.getClass().getSimpleName()).append("(this, ");
		
		return codeBuilder;
	}
	
	public boolean isScriptComment() {
		return false;
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------

	public void execute(ActionTestScript ts){
		execute(ts.getCurrentChannel());
		ts.getRecorder().createVisualAction(this);
	}
	
	public void execute(Channel channel){
		if(channel == null) {
			setStatus(new ActionStatus(null));
		}else {
			setStatus(channel.newActionStatus());
		}
	}

	//--------------------------------------------------------
	// getters and setters for serialization
	//--------------------------------------------------------

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public ActionStatus getStatus() {
		return status;
	}

	public void setStatus(ActionStatus status) {
		this.status = status;
	}
}
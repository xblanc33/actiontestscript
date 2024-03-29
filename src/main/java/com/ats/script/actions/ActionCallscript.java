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

import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ats.executor.ActionTestScript;
import com.ats.generator.variables.CalculatedValue;
import com.ats.generator.variables.Variable;
import com.ats.script.ProjectData;
import com.ats.script.Script;
import com.ats.script.ScriptLoader;
import com.ats.tools.AtsClassLoader;
import com.ats.tools.Utils;
import com.ats.tools.logger.MessageCode;

public class ActionCallscript extends Action {

	public static final String SCRIPT_LABEL = "subscript";

	private static final String SCRIPT_LOOP = "loop";
	public static final Pattern LOOP_REGEXP = Pattern.compile(SCRIPT_LOOP + " ?\\((\\d+)\\)", Pattern.CASE_INSENSITIVE);
	private static final String ASSETS_PROTOCOLE = "assets:///";
	private static final String FILE_PROTOCOLE = "file:///";
	private static final String HTTP_PROTOCOLE = "http://";
	private static final String HTTPS_PROTOCOLE = "https://";

	private final AtsClassLoader classLoader = new AtsClassLoader();

	private CalculatedValue name;

	private List<Variable> variables;
	private List<CalculatedValue> parameters;
	private int loop = 1;
	private String csvFilePath = null;

	public ActionCallscript() {}

	public ActionCallscript(ScriptLoader script, String name, String[] parameters, String[] returnValue) {

		super(script);
		setName(new CalculatedValue(script, name));

		if(parameters != null && parameters.length > 0) {

			String firstParam = parameters[0];
			if(firstParam.startsWith(ASSETS_PROTOCOLE) || firstParam.startsWith(FILE_PROTOCOLE) || firstParam.startsWith(HTTP_PROTOCOLE) || firstParam.startsWith(HTTPS_PROTOCOLE)) {
				setCsvFilePath(firstParam);
			}else {
				ArrayList<CalculatedValue> paramsValues = new ArrayList<CalculatedValue>();
				for(String param : parameters){

					param = param.replaceAll("\n", ",");

					Matcher match = LOOP_REGEXP.matcher(param);
					if(match.find()){
						try{
							this.loop = Integer.parseInt(match.group(1));
						}catch (NumberFormatException e){}
					}else {
						paramsValues.add(new CalculatedValue(script, param.trim()));
					}
				}
				setParameters(paramsValues);
			}
		}

		if(returnValue != null && returnValue.length > 0 && this.loop == 1){
			ArrayList<Variable> variableValues = new ArrayList<Variable>();
			for (String varName : returnValue ){
				variableValues.add(script.getVariable(varName.trim(), true));
			}
			setVariables(variableValues);
		}
	}

	public ActionCallscript(Script script, CalculatedValue name) {
		super(script);
		setName(name);
	}

	public ActionCallscript(Script script, CalculatedValue name, CalculatedValue[] parameters) {
		this(script, name);
		setParameters(new ArrayList<CalculatedValue>(Arrays.asList(parameters)));
	}

	public ActionCallscript(Script script, CalculatedValue name, Variable ... variables) {
		this(script, name);
		setVariables(new ArrayList<Variable>(Arrays.asList(variables)));
	}

	public ActionCallscript(Script script, CalculatedValue name, CalculatedValue[] parameters, Variable ... variables) {
		this(script, name);
		setParameters(new ArrayList<CalculatedValue>(Arrays.asList(parameters)));
		setVariables(new ArrayList<Variable>(Arrays.asList(variables)));
	}

	public ActionCallscript(Script script, CalculatedValue name, String csvFilePath) {
		this(script, name);
		setCsvFilePath(csvFilePath);
	}

	public ActionCallscript(Script script, CalculatedValue name, CalculatedValue[] parameters, int loop) {
		this(script, name, parameters);
		setLoop(loop);
	}

	public ActionCallscript(Script script, CalculatedValue name, int loop) {
		this(script, name);
		setLoop(loop);
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	// Code Generator
	//---------------------------------------------------------------------------------------------------------------------------------

	@Override
	public StringBuilder getJavaCode() {

		StringBuilder codeBuilder = super.getJavaCode();
		codeBuilder.append(name.getJavaCode());

		if(csvFilePath != null) {
			codeBuilder.append(", \"")
			.append(csvFilePath)
			.append("\"");
		}else {
			if(parameters != null){
				StringJoiner joiner = new StringJoiner(", ");
				for (CalculatedValue value : parameters){
					joiner.add(value.getJavaCode());
				}
				codeBuilder.append(", ")
				.append(ActionTestScript.JAVA_PARAM_FUNCTION_NAME)
				.append("(")
				.append(joiner.toString())
				.append(")");
			}

			if(loop > 1) {
				codeBuilder.append(", ")
				.append(loop);
			}else if(variables != null){
				StringJoiner joiner = new StringJoiner(", ");
				for (Variable variable : variables){
					joiner.add(variable.getName());
				}
				codeBuilder.append(", ")
				.append(joiner.toString());
			}
		}

		codeBuilder.append(")");

		return codeBuilder;
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------

	@Override
	public void execute(ActionTestScript ts) {

		super.execute(ts.getCurrentChannel());

		//Class<ActionTestScript> clazz = (Class<ActionTestScript>) Class.forName(name.getCalculated()); // old way still working
		Class<ActionTestScript> clazz = classLoader.findClass(name.getCalculated());

		if(clazz == null) {

			status.setPassed(false);
			status.setCode(MessageCode.SCRIPT_NOT_FOUND);
			status.setMessage("ATS script not found : '" + name.getCalculated() + "' (maybe a letter case issue ?)\n");

		}else {

			try {

				ActionTestScript ats = clazz.getDeclaredConstructor().newInstance();

				if(csvFilePath != null) {

					URL csvUrl = null;

					if(csvFilePath.startsWith(ASSETS_PROTOCOLE)) {
						csvUrl = getClass().getClassLoader().getResource(csvFilePath.replace(ASSETS_PROTOCOLE, ProjectData.ASSETS_FOLDER + File.separator));
					}else {
						try {
							csvUrl = new URL(csvFilePath);
						} catch (MalformedURLException e) {}
					}

					if(csvUrl == null) {
						status.setPassed(false);
						status.setMessage("CSV file not found : " + csvFilePath);
						return;
					}

					try {

						ArrayList<String[]> data = Utils.loadCsvData(csvUrl);

						for (String[] param : data) {
							ts.getTopScript().sendInfo("Call subscript -> ", name.getCalculated());

							ats.initCalledScript(ts.getTopScript(), param, null);
							Method testMain = clazz.getDeclaredMethod(ActionTestScript.MAIN_TEST_FUNCTION, new Class[]{});
							testMain.invoke(ats);
						}

					} catch (IOException e) {
						status.setPassed(false);
						status.setMessage("CSV file IO error : " + csvFilePath + " -> " + e.getMessage());
					}

				}else {
					ats.initCalledScript(ts.getTopScript(), getCalculatedParameters(), variables);
					Method testMain = clazz.getDeclaredMethod(ActionTestScript.MAIN_TEST_FUNCTION, new Class[]{});
					for (int i=0; i<loop; i++) {
						ts.getTopScript().sendInfo("call subscript -> ", name.getCalculated());
						testMain.invoke(ats);
					}

					status.setData(ats.getReturnValues());
				}

			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {

				if(e.getTargetException() instanceof AssertionError) {
					fail(e.getCause().getMessage());
				}

			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}
		
		status.endDuration();
	}

	private String[] getCalculatedParameters() {
		if(parameters != null) {
			int index = 0;
			String[] calculatedParameters = new String[parameters.size()];
			for(CalculatedValue calc : parameters) {
				calculatedParameters[index] = calc.getCalculated();
				index++;
			}
			return calculatedParameters;
		}
		return null;
	}

	//--------------------------------------------------------
	// getters and setters for serialization
	//--------------------------------------------------------

	public CalculatedValue getName() {
		return name;
	}

	public void setName(CalculatedValue name) {
		this.name = name;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> value) {
		this.variables = value;
		if(value != null) {
			this.csvFilePath = null;
			this.loop = 1;
		}
	}

	public List<CalculatedValue> getParameters() {
		return parameters;
	}

	public void setParameters(List<CalculatedValue> value) {
		this.parameters = value;
		if(value != null) {
			this.csvFilePath = null;
		}
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {

		if(loop <= 0) {
			loop = 1;
		}

		if(loop > 1) {
			this.csvFilePath = null;
			this.variables = null;
		}
		this.loop = loop;
	}

	public String getCsvFilePath() {
		return csvFilePath;
	}

	public void setCsvFilePath(String value) {
		this.csvFilePath = value;
		if(value != null) {
			this.parameters = null;
			this.variables = null;
			this.loop = 1;
		}
	}	
}
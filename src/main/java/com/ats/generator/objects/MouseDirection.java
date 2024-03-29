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

package com.ats.generator.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ats.executor.ActionTestScript;

public class MouseDirection {

	public static final Pattern POSITION_REGEXP = Pattern.compile("(" + Cartesian.RIGHT + "|" + Cartesian.TOP + "|" + Cartesian.LEFT + "|" + Cartesian.BOTTOM + "|" + Cartesian.MIDDLE + "|" + Cartesian.CENTER + ")\\s*?\\((-?\\d+)\\)", Pattern.CASE_INSENSITIVE);

	private MouseDirectionData horizontalPos;
	private MouseDirectionData verticalPos;

	public MouseDirection() {}
	
	public MouseDirection(ArrayList<String> options, boolean canBeEmpty) {
		Iterator<String> itr = options.iterator();
		while (itr.hasNext()){
			if(addPosition(itr.next())){
				itr.remove();
			}
		}
		
		if(!canBeEmpty && this.horizontalPos == null && this.verticalPos == null) {
			this.setHorizontalPos(new MouseDirectionData(Cartesian.RIGHT, 20));
		}
	}

	public MouseDirection(MouseDirectionData hpos, MouseDirectionData vpos) {
		setHorizontalPos(hpos);
		setVerticalPos(vpos);
	}

	public boolean addPosition(String value) {
	  
		Matcher match = POSITION_REGEXP.matcher(value);
		if(match.find()){
			
			String name = match.group(1);
			String pos = match.group(2);
			
			if(Cartesian.RIGHT.equals(name) || Cartesian.LEFT.equals(name) || Cartesian.CENTER.equals(name)){
				setHorizontalPos(new MouseDirectionData(name, pos));
			}else if(Cartesian.TOP.equals(name) || Cartesian.BOTTOM.equals(name) || Cartesian.MIDDLE.equals(name)){
				setVerticalPos(new MouseDirectionData(name, pos));
			}
			return true;
		}
		
		return false;
	}
	
	public int getHorizontalDirection() {
		if(horizontalPos != null) {
			return horizontalPos.getHorizontalDirection();
		}
		return 0;
	}
	
	public int getVerticalDirection() {
		if(verticalPos != null) {
			return verticalPos.getVerticalDirection();
		}
		return 0;
	}
	
	public void updateForDrag() {
		setHorizontalPos(null);
		setVerticalPos(new MouseDirectionData(Cartesian.BOTTOM, -20));
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	// Code Generator
	//---------------------------------------------------------------------------------------------------------------------------------

	private String getJavaCode(ArrayList<String> codeData) {
		if(horizontalPos != null || verticalPos != null) {
			if(horizontalPos != null){
				codeData.add(ActionTestScript.JAVA_POS_FUNCTION_NAME + "(" + horizontalPos.getJavaCode() + ")");
			}else {
				codeData.add("null");
			}
			
			if(verticalPos != null){
				codeData.add(ActionTestScript.JAVA_POS_FUNCTION_NAME + "(" + verticalPos.getJavaCode() + ")");
			}else {
				codeData.add("null");
			}
			return String.join(", ", codeData);
		}
		return "";
	}
	
	public String getPositionJavaCode() {
		return getJavaCode(new ArrayList<String>(Arrays.asList(new String[]{""})));
	}
	
	public String getDirectionJavaCode() {
		return getJavaCode(new ArrayList<String>());
	}

	//----------------------------------------------------------------------------------------------------------------------
	// Getter and setter for serialization
	//----------------------------------------------------------------------------------------------------------------------

	public MouseDirectionData getHorizontalPos() {
		return horizontalPos;
	}

	public void setHorizontalPos(MouseDirectionData horizontalPos) {
		this.horizontalPos = horizontalPos;
	}

	public MouseDirectionData getVerticalPos() {
		return verticalPos;
	}

	public void setVerticalPos(MouseDirectionData verticalPos) {
		this.verticalPos = verticalPos;
	}
}
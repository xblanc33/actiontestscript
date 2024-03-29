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

package com.ats.element;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import com.ats.element.api.AtsApiElement;
import com.ats.executor.TestBound;
import com.ats.executor.channels.Channel;
import com.ats.executor.drivers.desktop.DesktopWindow;

public class FoundElement{

	public final static String HTML = "html";
	public final static String DESKTOP = "desktop";
	public final static String MOBILE = "mobile";
	public final static String API = "api";

	private String id;

	private Double x = 0.0;
	private Double y = 0.0;

	private Double screenX = 0.0;
	private Double screenY = 0.0;

	private Double boundX = 0.0;
	private Double boundY = 0.0;

	private Double width = 0.0;
	private Double height = 0.0;

	private String tag;

	private String type = HTML;

	private RemoteWebElement value;
	private FoundElement parent;
	private boolean visible = true;
	private boolean numeric = false;
	private boolean password = false;
	private boolean clickable = true;

	private ArrayList<AtsElement> iframes;
	
	//------------------------------------------------------------------------------------------------------------------------------
	// contructors
	//------------------------------------------------------------------------------------------------------------------------------

	public FoundElement() {}

	public FoundElement(Channel channel) {
		this.setRemoteWebElement((RemoteWebElement) channel.getRootElement());
		this.width = channel.getDimension().getWidth();
		this.height = channel.getDimension().getHeight();
	}

	public FoundElement(AtsElement element) {
		this.setRemoteWebElement(element.getElement());
		this.tag = element.getTag();
		this.width = element.getWidth();
		this.height = element.getHeight();
		this.numeric = element.isNumeric();
		this.password = element.isPassword();
	}

	public FoundElement(DesktopWindow win) {
		this.id = win.getId();
		this.tag = win.getTag();
		this.width = win.getWidth();
		this.height = win.getHeight();
	}
	
	public FoundElement(AtsElement element, Channel channel, Double offsetX, Double offsetY) {

		this(element);
		
		final Double elemX = element.getX();
		final Double elemY = element.getY();

		this.updatePosition(elemX, elemY, channel, offsetX, offsetY);
		this.screenX = element.getScreenX() + elemX;
		this.screenY = element.getScreenY() + elemY;
		this.boundX = element.getBoundX();
		this.boundY = element.getBoundY();
	}

	public FoundElement(AtsElement element, ArrayList<AtsElement> iframes, Channel channel, Double offsetX, Double offsetY) {
		this(element, channel, offsetX, offsetY);
		this.iframes = iframes;
	}

	public FoundElement(AtsElement element, TestBound channelDimension) {

		this.type = DESKTOP;
		this.visible = element.isVisible();
		this.id = element.getId();

		this.tag = element.getTag();
		this.width = element.getWidth();
		this.height = element.getHeight();

		this.screenX = element.getX();
		this.screenY = element.getY();

		this.x = this.screenX - channelDimension.getX();
		this.y = this.screenY - channelDimension.getY();
	}

	public FoundElement(AtsMobileElement element) {

		this.type = MOBILE;
		this.visible = true;
		this.id = element.getId();

		this.tag = element.getTag();
		this.width = element.getWidth();
		this.height = element.getHeight();

		this.clickable = element.isClickable();

		this.screenX = element.getX();
		this.screenY = element.getY();

		this.x = this.screenX;
		this.y = this.screenY;
	}

	public FoundElement(AtsApiElement element) {
		this.type = API;
		this.visible = true;
		this.id = element.getId();
		this.tag = element.getTag();
		this.clickable = false;
	}
	
	public FoundElement(Channel channel, ArrayList<AtsElement> iframes, ArrayList<AtsElement> elements, Double initElementX, Double initElementY) {
		this(elements.remove(0), iframes, channel, initElementX, initElementY);
		if(elements.size() > 0) {
			setParent(new FoundElement(channel, iframes, elements, initElementX, initElementY));
		}else if(iframes.size() > 0) {
			elements.addAll(iframes);
			iframes.clear();
			setParent(new FoundElement(channel, iframes, elements, initElementX, initElementY));
		}
	}

	public boolean isDesktop() {
		return DESKTOP.equals(type);
	}
	
	public ArrayList<AtsElement> getIframes(){
		return iframes;
	}

	//------------------------------------------------------------------------------------------------------------------------------
	// end of contructors
	//------------------------------------------------------------------------------------------------------------------------------

	public void dispose() {
		if(parent != null) {
			parent.dispose();
			parent = null;
		}
		value = null;
	}

	public void updatePosition(Double x, Double y, Channel channel, Double offsetX, Double offsetY) {
		this.setX(x + channel.getSubDimension().getX() + offsetX);
		this.setY(y + channel.getSubDimension().getY() + offsetY);
	}

	private void setRemoteWebElement(RemoteWebElement rwe){
		this.value = rwe;
		if(rwe != null) {
			this.id = rwe.getId();
		}
	}

	public boolean isIframe(){
		return AtsElement.checkIframe(this.tag);
	}

	public WebElement getValue(){
		return value;
	}

	public void activate(RemoteWebDriver remoteWebDriver) {
		value = new RemoteWebElement();
		value.setParent(remoteWebDriver);
		value.setId(id);
	}

	public Rectangle getRectangle(){
		return new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
	}

	public Double getScreenX() {
		return screenX;
	}

	public Double getScreenY() {
		return screenY;
	}

	public boolean isNumeric() {
		return numeric;
	}
	
	public boolean isPassword() {
		return password;
	}

	//----------------------------------------------------------------------------------------------------------------------
	// Getter and setter for serialization
	//----------------------------------------------------------------------------------------------------------------------

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean value){
		this.clickable = value;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean value){
		this.visible = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FoundElement getParent() {
		return parent;
	}

	public void setParent(FoundElement parent) {
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Double getBoundX() {
		return boundX;
	}

	public void setBoundX(Double boundX) {
		this.boundX = boundX;
	}

	public Double getBoundY() {
		return boundY;
	}

	public void setBoundY(Double boundY) {
		this.boundY = boundY;
	}

	//----------------------------------------------------------------------------------------------------------------------
	// Remote element
	//----------------------------------------------------------------------------------------------------------------------

	public RemoteWebElement getRemoteWebElement(RemoteWebDriver driver) {
		final RemoteWebElement element = new RemoteWebElement();

		element.setParent(driver);
		element.setId(id);

		return element;
	}

	public TestBound getTestBound() {
		return new TestBound(x, y, width, height);
	}
}
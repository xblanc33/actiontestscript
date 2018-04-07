package com.ats.tools;

import java.io.IOException;
import java.util.Base64;

import com.ats.driver.AtsManager;
import com.ats.executor.TestBound;
import com.google.common.io.Resources;

public final class StartHtmlPage {

	private static final String atsBrowserTitle = "ats-automation-enabled";

    public static String getAtsBrowserTitle() {
    	return atsBrowserTitle;
    }
    
    public static byte[] getAtsBrowserContent(String applicationVersion, String driverVersion, TestBound testBound) {
    	
    	if(driverVersion == null) {
    		driverVersion = "N/A";
    	}

    	StringBuilder htmlContent = new StringBuilder();
    	htmlContent.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>");
    	htmlContent.append(atsBrowserTitle);
    	htmlContent.append("</title><style>span {font-family: Arial; color: #606b6f; text-shadow: 2px 2px 12px rgba(96,107,111,0.6)}</style></head><body bgcolor=\"#f2f2f2\"><a href=\"https://www.actiontestscript.com\"><img src=\"data:image/png;base64, ");
    	
    	try {
    		htmlContent.append(Base64.getEncoder().encodeToString(Resources.toByteArray(ResourceContent.class.getResource("/icon/ats_power.png"))));
		} catch (IOException e1) {}
    	
    	htmlContent.append("\" alt=\"ActionTestScript\"/></a>");
    	htmlContent.append("<div style=\"padding-left: 40px;\"><span>- ActionTestScript version : ");
    	htmlContent.append(AtsManager.getVersion());
    	htmlContent.append("&nbsp;</span></div>");
    	
    	htmlContent.append("<div style=\"padding-left: 40px;\"><span>- Browser version : ");
    	htmlContent.append(applicationVersion);
    	htmlContent.append("&nbsp;</span></div>");
    	
    	htmlContent.append("<div style=\"padding-left: 40px;\"><span>- Driver version : ");
    	htmlContent.append(driverVersion);
    	htmlContent.append("&nbsp;</span></div>");
    	
    	htmlContent.append("<div style=\"padding-left: 40px;\"><span>- Bounding box : [ ");
    	htmlContent.append(testBound.getX());
    	htmlContent.append(" : ");
    	htmlContent.append(testBound.getY());
    	htmlContent.append("&nbsp;&nbsp;&nbsp;&nbsp;");
    	htmlContent.append(testBound.getWidth());
    	htmlContent.append(" x ");
    	htmlContent.append(testBound.getHeight());

    	htmlContent.append(" ]&nbsp;</span></div>");
    	
    	htmlContent.append("</body></html>");
    			    	
    	return htmlContent.toString().getBytes();
    }
}

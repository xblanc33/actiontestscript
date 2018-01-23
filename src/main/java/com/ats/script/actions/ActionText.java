package com.ats.script.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ats.element.SearchedElement;
import com.ats.executor.ActionTestScript;
import com.ats.executor.SendKeyData;
import com.ats.generator.objects.MouseDirection;
import com.ats.generator.variables.CalculatedValue;
import com.ats.script.Script;
import com.ats.script.ScriptLoader;

public class ActionText extends ActionExecuteElement {

	public static final String SCRIPT_LABEL = "keyboard";

	public static final Pattern INSERT_PATTERN = Pattern.compile("insert\\((\\d+)\\)", Pattern.CASE_INSENSITIVE);
	public static final Pattern KEY_REGEXP = Pattern.compile("\\$key\\s?\\((\\w+)\\-?([^\\)]*)?\\)");
	
	private CalculatedValue text;

	private int insert = -1;

	public ActionText() {}

	public ActionText(ScriptLoader script, String type, boolean stop, ArrayList<String> options, String text, ArrayList<String> objectArray) {
		super(script, stop, options, objectArray);
		this.text = new CalculatedValue(script, text);

		Iterator<String> itr = options.iterator();
		while (itr.hasNext())
		{
			String data = itr.next();
			Matcher matcher = INSERT_PATTERN.matcher(data);
			if(matcher.find()){
				try {
					setInsert(Integer.parseInt(matcher.group(1)));
				}catch(NumberFormatException e){}
				break;
			}
		}
	}
	
	public ActionText(Script script, boolean stop, int maxTry, SearchedElement element, CalculatedValue text) {
		super(script, stop, maxTry, element);
		setText(text);
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	// Code Generator
	//---------------------------------------------------------------------------------------------------------------------------------

	@Override
	public String getJavaCode() {
		return super.getJavaCode() + ", " + text.getJavaCode() + ")";
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------

	@Override
	public void terminateExecution(ActionTestScript ts) {

		super.terminateExecution(ts);
		String dataText = "";
		if(text != null){
			dataText = text.getCalculated();
		}

		ts.updateVisualValue(dataText);
		enterText(dataText);
			
		ts.updateVisualImage();
	}

	public void enterText(String value) {
		status.resetDuration();

		getTestElement().over(status, new MouseDirection());
		if(status.isPassed()) {
			getTestElement().click(status, false);
			if(status.isPassed()) {
				getTestElement().sendText(status, insert == -1, getTextChain(value));
			}
		}
		
		status.updateDuration();
	}

	//--------------------------------------------------------
	// getters and setters for serialization
	//--------------------------------------------------------	

	public CalculatedValue getText() {
		return text;
	}

	public void setText(CalculatedValue text) {
		this.text = text;
	}

	public int getInsert() {
		return insert;
	}

	public void setInsert(int insert) {
		this.insert = insert;
	}

	//--------------------------------------------------------
	// Utils
	//--------------------------------------------------------	

	public static ArrayList<SendKeyData> getTextChain(String s){

		ArrayList<SendKeyData> result = new ArrayList<SendKeyData>();
		int start = 0;		

		Matcher match = KEY_REGEXP.matcher(s);
		while(match.find()) {

			int end = match.start();
			if(end > 0) {
				SendKeyData sendKey = new SendKeyData(s.substring(start, end));
				result.add(sendKey);
			}

			start = match.end();

			String keysName = match.group(1);
			String spareKey = match.group(2);
			if(spareKey != null && spareKey.length() > 0) {
				result.add(new SendKeyData(new String[] {keysName, spareKey}));
			}else {
				result.add(new SendKeyData(new String[] {keysName}));
			}
		}

		SendKeyData sendKey = null;
		if(start == 0) {
			sendKey = new SendKeyData(s);
		}else if(start != s.length()){
			sendKey = new SendKeyData(s.substring(start));
		}
		
		if(sendKey != null) {
			result.add(sendKey);
		}

		return result;
	}
}
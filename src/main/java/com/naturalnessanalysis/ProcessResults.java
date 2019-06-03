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

package com.naturalnessanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.naturalness.NaturalnessModel;
import com.naturalness.Ranking;
import org.xml.sax.SAXException;

import com.ats.generator.ATS;
import com.ats.tools.XmlReport;

public class ProcessResults {
	final int DEPTH = 5;
	final double PROBA_OF_UNKNOWN = 1e-6;

	private Analyzer analyzer;
	
	public static void main(String[] args) {
		if(args.length > 0) {
			new ProcessResults(args[0]);
		}else {
			ATS.logError("No xml reports folder defined !");
		}
	}
	
	private ArrayList<File> xmlReports = new ArrayList<File>();

	public ProcessResults(String reportsFolderPath) {
		this(new File(reportsFolderPath));
	}

	public ProcessResults(File reportsFolder) {
		analyzer = new Analyzer(DEPTH, PROBA_OF_UNKNOWN);

		if(!reportsFolder.exists()){
			ATS.logError("xml reports folder does not exists -> " + reportsFolder.getAbsolutePath());
			return;
		}

		if(!reportsFolder.isDirectory()){
			ATS.logError("This path is not a directory -> " + reportsFolder.getAbsolutePath());
			return;
		}

		try {
			Files.find(
				reportsFolder.toPath(), 
				99999, 
				(p, f) -> f.isRegularFile() && XmlReport.REPORT_FILE.equals(p.toFile().getName())
			).forEach(p -> xmlReports.add(p.toFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//-----------------------------------------------------------------------------------------------------------------
		// List of xml reports files is populated
		//-----------------------------------------------------------------------------------------------------------------
		
		for (File xml : xmlReports) {
			final String testName = xml.getParentFile().getName().replaceAll("\\_xml$", "");
			
			//-----------------------------------------------------------------------------------------------------------------
			// now we have the name of the executed test and the xml report of the test		
			//-----------------------------------------------------------------------------------------------------------------
			
			try {
				analyzer.recordSequenceFromLogFile(xml);
				System.out.println("Xml report -> " + testName + " learnt ");
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		NaturalnessModel<String> model = analyzer.learn();
		int modelSize = model.size();
		System.out.println(modelSize+" ngrams have been extracted");

		int modelOccurence = model.occurence();
		System.out.println(modelOccurence+" occurences have been extracted");

		double redundancyRatio = (double) modelOccurence / modelSize;
		System.out.println("The redundancy ratio is : "+redundancyRatio);

		System.out.println("Ranking");
		List<Ranking> rankingList = analyzer.rank();
		for (Ranking rank : analyzer.rank()) {
			String name = analyzer.getSequenceName(rank.getSequence());
			double crossEntropy = rank.getCrossEntropy();
			System.out.println("Test "+name+" has "+crossEntropy+" entropy");
		}
	}
}
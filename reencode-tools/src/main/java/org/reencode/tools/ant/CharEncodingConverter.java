package org.reencode.tools.ant;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.LogLevel;
import org.reencode.CharacterEncodingConversion;

/**
 *  Copyright 2012 Nick A. Watts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * This class implements an Ant task that supports the re-encoding of one
 * or more text files into a new character encoding.
 * 
 * @author Nick Watts
 *
 */
public class CharEncodingConverter extends Task {
	private String inputEncoding;
	private String outputEncoding;
	private String todir;
	private boolean verbose;
	private CharacterEncodingConversion cec;
	private Vector<FileSet> filesets = new Vector<FileSet>();

	public CharEncodingConverter() {
		cec = new CharacterEncodingConversion();
	}
	
	public void validate() {
		if( inputEncoding == null || inputEncoding.isEmpty() ) {
			throw new BuildException("inputEncoding is required.");
		}
		if( outputEncoding == null || outputEncoding.isEmpty() ) {
			throw new BuildException("outputEncoding is required.");
		}
		if( todir == null || todir.isEmpty() ) {
			throw new BuildException("todir is required.");
		}
	}
	
	public void execute() {
		Integer convertedFilesCount = new Integer(0);
		validate();
		createTodirPath();
		
		for( FileSet fileset : filesets ) {
			DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
			List<String> includedFiles = Arrays.asList(ds.getIncludedFiles());
			for( String filename : includedFiles ) {
				File inputFile = new File(ds.getBasedir(), filename);
				File outputFile = new File(todir, filename);
				
				try {
					cec.reencodeFile(inputFile.getAbsolutePath(), 
									 outputFile.getAbsolutePath(), 
									 inputEncoding, outputEncoding);
					convertedFilesCount++;
					if( verbose ) {
						log("Re-encoded "+inputFile.getName()+" to " + outputFile.getPath(), 
							LogLevel.INFO.getLevel());
					}
				} catch (Exception e) {
					log(e, LogLevel.ERR.getLevel());
					throw new BuildException("Error processing file "+inputFile.getAbsolutePath(),	
											 getLocation());
				}
			}
		}
		
		log("Successfully converted " + convertedFilesCount + 
			" files from character encoding " + inputEncoding +
			" to " + outputEncoding + ".",
			LogLevel.INFO.getLevel());
	}

	private void createTodirPath() {
		File outputDir = new File(todir);
		outputDir.mkdirs();
		outputDir.setWritable(true);
	}

	public void addFileset(FileSet fileset) { 
		filesets.add(fileset);
	}
	
	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public void setTodir(String todir) {
		this.todir = todir;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}

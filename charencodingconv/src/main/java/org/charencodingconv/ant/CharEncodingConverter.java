package org.charencodingconv.ant;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.LogLevel;
import org.charencodingconv.CharacterEncodingConversion;

public class CharEncodingConverter extends Task {
	private String inputEncoding;
	private String outputEncoding;
	private String outputDir;
	private CharacterEncodingConversion cec;
	private Vector<FileSet> filesets = new Vector<FileSet>();

	public CharEncodingConverter() {
		cec = new CharacterEncodingConversion();
	}
	
	public void validate() {
		if( StringUtils.isBlank(inputEncoding) ) {
			throw new BuildException("inputEncoding is required.");
		}
		if( StringUtils.isBlank(outputEncoding) ) {
			throw new BuildException("outputEncoding is required.");
		}
		if( StringUtils.isBlank(outputDir) ) {
			throw new BuildException("outputDir is required.");
		}
	}
	
	public void execute() {
		validate();
		
		for( FileSet fileset : filesets ) {
			DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
			List<String> includedFiles = Arrays.asList(ds.getIncludedFiles());
			for( String file : includedFiles ) {
				String filename = file.replace('\\','/'); 
				filename = filename.substring(filename.lastIndexOf("/")+1);
				File inputFile = new File(ds.getBasedir(), filename);
				File outputFile = new File(outputDir, filename);
				outputFile.mkdirs();
				outputFile.setWritable(true);
				log("Converting '" + inputFile.getPath() + "' to '" + outputFile.getPath() + "'.", 
						LogLevel.INFO.getLevel());
				
				try {
					cec.reencodeFile(inputFile.getAbsolutePath(), 
									 outputFile.getAbsolutePath(), 
									 inputEncoding, outputEncoding);
					log("Re-encoded "+inputFile.getName()+" to " + outputFile.getPath());
				} catch (Exception e) {
					log(e, LogLevel.ERR.getLevel());
					throw new BuildException("Error processing file "+inputFile.getAbsolutePath(),	
											 getLocation());
				}
			}
		}
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

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
}

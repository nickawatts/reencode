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
	private String todir;
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
		if( StringUtils.isBlank(todir) ) {
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
			for( String file : includedFiles ) {
				String filename = extractFilename(file);
				File inputFile = new File(ds.getBasedir(), filename);
				File outputFile = new File(todir, filename);
				
				try {
					cec.reencodeFile(inputFile.getAbsolutePath(), 
									 outputFile.getAbsolutePath(), 
									 inputEncoding, outputEncoding);
					convertedFilesCount++;
					log("Re-encoded "+inputFile.getName()+" to " + outputFile.getPath(), 
						LogLevel.VERBOSE.getLevel());
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

	private String extractFilename(String file) {
		String filename = file.replace('\\','/'); 
		filename = filename.substring(filename.lastIndexOf("/")+1);
		return filename;
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
}

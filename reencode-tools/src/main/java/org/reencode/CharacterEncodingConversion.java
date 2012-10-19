package org.reencode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Copyright 2012 Nick A. Watts
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
 * This class offers a method for re-encoding a text file under a new
 * character encoding.
 * 
 * @author Nick Watts
 *
 */
public class CharacterEncodingConversion {
	private final int chunkSize = 4096;
	
	/**
	 * A very simple main method that can be used to re-encode a file.
	 * @param args Four arguments are expected in the following order:
	 * inputFile outputFile inputEncoding outputEncoding.
	 */
	public static void main(String[] args) throws Exception {
		String inputFile = args[0];
		String outputFile = args[1];
		String inputEncoding = args[2];
		String outputEncoding = args[3];
		
		CharacterEncodingConversion cec = new CharacterEncodingConversion();
		cec.reencodeFile(inputFile, outputFile, inputEncoding, outputEncoding);
	}

	/**
	 * Take a file that is encoded as <code>inputEncoding</code> and re-encode it
	 * as <code>outputEncoding</code>. This method is not very intelligent, passing
	 * off any exceptions that are thrown to the caller. The classes
	 * {@link CharsetDecoder} and {@link CharsetEncoder} are used to do the work of
	 * re-encoding the input file. 
	 * 
	 * The encodings passed in as <code>inputEncoding</code> and <code>outputEncoding</code>
	 * must be one of the strings listed in the Java API docs 
	 * <a href="http://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html">here</a>.
	 * Common and valid examples are UTF-8, ISO-8859-1 and CP1252.
	 *  
	 * @param inputFile The file to be re-encoded (has the encoding <code>inputEncoding</code>.
	 * @param outputFile The re-encoded file (has the encoding <code>outputEncoding</code>.
	 * @param inputEncoding The encoding of the <code>inputFile</code>. This method does not
	 * verify that this encoding is correct.
	 * @param outputEncoding The encoding to use when re-encoding the <code>inputFile</code>
	 * as the <code>outputFile</code>.
	 * @throws Exception Any exception thrown during processing is passed off to the caller.
	 * @since 0.1
	 */
	public void reencodeFile(String inputFile, String outputFile, 
						     String inputEncoding, String outputEncoding)
	throws Exception
	{
		Charset inputCharset = Charset.forName(inputEncoding);
		Charset outputCharset = Charset.forName(outputEncoding);
		CharsetDecoder decoder = inputCharset.newDecoder();
		CharsetEncoder encoder = outputCharset.newEncoder();
		
		FileChannel inChannel = new FileInputStream(inputFile).getChannel();
		FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
		ByteBuffer inBuff = ByteBuffer.allocate(chunkSize);
		ByteBuffer outBuff = ByteBuffer.allocate(chunkSize);
		CharBuffer decodedBuff = CharBuffer.allocate(chunkSize);
		
		decoder.reset();
		encoder.reset();
		while( inChannel.read(inBuff) != -1 ) {
			inBuff.flip();
			decoder.decode(inBuff, decodedBuff, false);
			decodedBuff.flip();
			encoder.encode(decodedBuff, outBuff, false);
			outBuff.flip();
			while( outBuff.hasRemaining() ) {
				outChannel.write(outBuff);
			}
			inBuff.clear();
			decodedBuff.clear();
			outBuff.clear();
		}
		decoder.decode(inBuff, decodedBuff, true);
		encoder.encode(decodedBuff, outBuff, true);
		decoder.flush(decodedBuff);
		encoder.flush(outBuff);
		inChannel.close();
		outChannel.close();		
	}
}

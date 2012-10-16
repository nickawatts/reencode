package org.charencodingconv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class CharacterEncodingConversion {
	private final int chunkSize = 4096;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String inputFile = args[0];
		String outputFile = args[1];
		String inputEncoding = args[2];
		String outputEncoding = args[3];
		
		CharacterEncodingConversion cec = new CharacterEncodingConversion();
		cec.reencodeFile(inputFile, outputFile, inputEncoding, outputEncoding);
	}

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

//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.testing.servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * A package level (ie non-public) helper to extract data from servlet response.
 *
 * 
 *
 */
class BufferServletOutputStream extends ServletOutputStream {
	/**
	 * Size of the default buffer.
	 *
	 */
	public static final int BUFFER_SIZE = 16384;

	private final ByteBuffer buffer;

	/**
	 * New instance, with a default buffer size.
	 *
	 */
	public BufferServletOutputStream() {
		this(BUFFER_SIZE);
	}

	/**
	 * Create a new instance, with a specific buffersize.
	 *
	 * @param bufferSize
	 *            max size of the buffer in bytes.
	 */
	public BufferServletOutputStream(int bufferSize) {
		buffer = ByteBuffer.allocate(bufferSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletOutputStream#isReady()
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.
	 * WriteListener)
	 */
	@Override
	public void setWriteListener(WriteListener writeListener) {
		// So nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int c) throws IOException {
		byte b = (byte) c;
		buffer.put(b);
	}

	/**
	 * Gets the internal buffer.
	 * 
	 * @return
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Get the content of the buffer as a string.
	 *
	 * @return the utf-8 string of the buffer.
	 */
	public String getAsString() {
		return new String(buffer.array(), 0, buffer.position(), StandardCharsets.UTF_8);
	}
}

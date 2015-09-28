//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic span type that can be sorted.
 * 
 * Comparing will order by start (earliest first), then end (earliest first) then value (as per String compare).
 * 
 * 
 *
 */
public class ComparableSpan implements Comparable<ComparableSpan> {
	private final int start;
	private final int end;
	private final String value;

	/** New instance with a start and end point.
	 * @param start offset in the text
	 * @param end offset in the text
	 */
	public ComparableSpan(int start, int end) {
		this(start, end, null);
	}

	/**
	 * @param start
	 * @param end
	 * @param value
	 */
	public ComparableSpan(int start, int end, String value) {
		this.start = start;
		this.end = end;
		this.value = value;
	}

	@Override
	public int compareTo(ComparableSpan s) {
		if (s.start > this.start) {
			return -1;
		} else if (s.start < this.start) {
			return 1;
		} else if (s.end > this.end) {
			return -1;
		} else if (s.end < this.end) {
			return 1;
		} else if(value == null && s.value == null) {
			return 0;
		} else if(value != null && s.value == null) {
			return 1;
		} else if(value == null && s.value != null) {
			return -1;	
		} else {
			return value.compareTo(s.value);
		}		
	}

	/** Get the start offset of this span.
	 * @return the start offset
	 */
	public int getStart() {
		return start;
	}

	/** Get the end offset of this span.
	 * @return the end offset
	 */
	public int getEnd() {
		return end;
	}

	/** Get the value associated with this value.
	 * 
	 * This may not be the text within the span!
	 * 
	 * @return the value (perhaps null)
	 */
	public String getValue() {
		return value;
	}
	
	/** Check is this span has a non-null value.
	 * @return boolean if value is non-null
	 */
	public boolean hasValue() {
		return value != null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComparableSpan other = (ComparableSpan) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%d:%d[%s]", start, end, value);
	}
	
	
	/** Build a set of comparable spans from the text using regex.
	 * 
	 * @param text the text to extract spans from 
	 * @param pattern the regex pattern to use to create extract terms
	 * @return a list (non-null, but possible empty) of matches. With regex gorup as value
	 */
	public static List<ComparableSpan> buildSpans(String text, Pattern pattern) {
		Matcher m = pattern.matcher(text);
		List<ComparableSpan> spans = new LinkedList<>();
		while(m.find()){
			ComparableSpan cs = new ComparableSpan(m.start(), m.end(), m.group());
			spans.add(cs);
		}
		return spans;
	}

	
}
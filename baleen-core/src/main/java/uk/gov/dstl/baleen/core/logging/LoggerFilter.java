//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter that can be used to filter based on the logger name (usually the class name).
 * 
 * Does the comparison using a startsWith() test, so that the filter can be used to filter by package as well.
 * To perform an exact match, then provide a NULL terminated string (e.g. \0)
 * 
 * 
 */
public class LoggerFilter extends Filter<ILoggingEvent> {
	private final List<String> loggerName;
	private final Boolean exclude;
	/**
	 * Contruct new LoggerFilter
	 * 
	 * @param loggerName The name of the logger to match against
	 * @param exclude Should matched loggers be included (false) or excluded (true)
	 */
	public LoggerFilter(String loggerName, Boolean exclude){
		this.loggerName = Lists.newArrayList(loggerName);
		this.exclude = exclude;
	}
	
	/**
	 * Construct new LoggerFilter
	 * 
	 * @param loggerName A list of loggers to match against
	 * @param exclude Should matched loggers be included (false) or excluded (true)
	 */
	public LoggerFilter(List<String> loggerName, Boolean exclude){
		this.loggerName = loggerName;
		this.exclude = exclude;
	}
	
	/**
	 * Decide whether or not to deny the event based on the settings of this filter
	 * 
	 * @param event The logging event to test
	 */
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if(Strings.isNullOrEmpty(event.getLoggerName())){
			return FilterReply.DENY;
		}
		
		for(String name : loggerName){
			FilterReply ret;
			if(name.endsWith("/0")){
				String nullTerminatedLN = event.getLoggerName()+"/0";
				ret = nameMatches(nullTerminatedLN.equals(name));
			}else{
				ret = nameMatches(event.getLoggerName().startsWith(name));
			}
			
			ret = shouldReturn(ret);
			if(ret != null){
				return ret;
			}
		}
		
		if(exclude){
			return FilterReply.NEUTRAL;
		}else{
			return FilterReply.DENY;
		}
	}
	
	private FilterReply shouldReturn(FilterReply ret){
		if(exclude && ret == FilterReply.DENY){
			return FilterReply.DENY;
		}else if (!exclude && ret == FilterReply.NEUTRAL){
			return FilterReply.NEUTRAL;
		}else{
			return null;
		}
	}

	/**
	 * Determine whether, on matching the logger name, we should reply DENY or NEUTRAL based on the current settings 
	 * 
	 * @param matches Does it match or not
	 * @return FilterReply.DENY or FilterReply.NEUTRAL
	 */
	private FilterReply nameMatches(boolean matches){
		if(matches){
			return exclude ? FilterReply.DENY : FilterReply.NEUTRAL;
		}else{
			return !exclude ? FilterReply.DENY : FilterReply.NEUTRAL;
		}
	}
	
	/**
	 * @return the loggerName
	 */
	public List<String> getLoggerName() {
		return loggerName;
	}

	/**
	 * @return the exclude
	 */
	public Boolean getExclude() {
		return exclude;
	}

}

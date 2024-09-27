package jsfas.common;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogbackExclusionFilter extends Filter<ILoggingEvent> {
	String loggerName;
	
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (event.getLoggerName().contains(loggerName)) {
			return FilterReply.DENY;
		} else {
			return FilterReply.NEUTRAL;
		}
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}
}

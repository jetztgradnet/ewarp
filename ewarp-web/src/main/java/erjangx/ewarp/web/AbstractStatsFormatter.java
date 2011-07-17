package erjangx.ewarp.web;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import erjangx.ewarp.runtime.stats.StatusName;

public abstract class AbstractStatsFormatter implements StatsFormatter {
	
	private final OutputFormat format;
	private final String contentType;

	public AbstractStatsFormatter(OutputFormat format, String contentType) {
		this.format = format;
		this.contentType = contentType;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.StatsFormatter#getFormat()
	 */
	public OutputFormat getFormat() {
		return format;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.StatsFormatter#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.StatsFormatter#writerOutput(java.io.PrintWriter, java.util.Map)
	 */
	public void writeOutput(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		startDocument(writer, statuses);
		
		for (Map.Entry<String, Map<StatusName, Object>> entry : statuses.entrySet()) {
			String collectorName = entry.getKey();
			Map<StatusName, Object> status = entry.getValue();
			writeOutput(writer, collectorName, status);
		}
		
		endDocument(writer, statuses);
	}

	public void writeOutput(PrintWriter writer, String collectorName,
			Map<StatusName, Object> status) {
		startSection(writer, collectorName, status);
		
		for (Map.Entry<StatusName, Object> entry : status.entrySet()) {
			StatusName name = entry.getKey();
			Object value = entry.getValue();
			
			writeStatusName(writer, name);
			writeValue(writer, name, value);
		}
		
		endSection(writer, collectorName, status);
	}

	protected void writeValue(PrintWriter writer, Object name, Object value) {
		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			if (map.size() > 0) {
				startCompoundValue(writer, name, value);
			}
			for (Entry<?, ?> subentry : map.entrySet()) {
				writeStatusName(writer, subentry.getKey());
				writeValue(writer, subentry.getKey(), subentry.getValue());
			}
			if (map.size() > 0) {
				endCompoundValue(writer, name, value);
			}
		}
		else if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			
			if (collection.size() > 0) {
				startCompoundValue(writer, name, value);
			}
			for (Object object : collection) {
				writeValue(writer, null, object);
			}
			if (collection.size() > 0) {
				endCompoundValue(writer, name, value);
			}
			
		}
		else {
			doWriteValue(writer, name, value);
		}
	}

	protected void startCompoundValue(PrintWriter writer, Object name, Object value) {
		// ignore
	}

	protected void endCompoundValue(PrintWriter writer, Object name, Object value) {
		// ignore
	}

	protected void writeStatusName(PrintWriter writer, Object name) {
		if (name instanceof StatusName) {
			StatusName statusName = (StatusName) name;
			doWriteStatusName(writer, statusName.getDisplayName());
		}
		else if (name != null) {
			doWriteStatusName(writer, name.toString());
		}
	}

	protected abstract void doWriteStatusName(PrintWriter writer, String name);
	
	protected void doWriteValue(PrintWriter writer, Object name, Object value) {
		String text = (value != null ? value.toString() : "");
		String escapedValue = escapeText(text);
		writer.print(escapedValue);
	}

	protected abstract void startSection(PrintWriter writer, String section, Map<StatusName, Object> status);
	
	protected void endSection(PrintWriter writer, String collectorName, Map<StatusName, Object> status) {
		// ignore
	}

	protected void endDocument(PrintWriter writer, Map<String, Map<StatusName, Object>> statuses) {
		// ignore
	}

	protected void startDocument(PrintWriter writer, Map<String, Map<StatusName, Object>> statuses) {
		// ignore
	}
	
	protected String escapeText(String text) {
		return text;
	}

}

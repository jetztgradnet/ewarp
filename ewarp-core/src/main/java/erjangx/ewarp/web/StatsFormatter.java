package erjangx.ewarp.web;

import java.io.PrintWriter;
import java.util.Map;

import erjangx.ewarp.runtime.stats.StatusName;

public interface StatsFormatter {
	OutputFormat getFormat();
	String getContentType();
	void writeOutput(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses);
}

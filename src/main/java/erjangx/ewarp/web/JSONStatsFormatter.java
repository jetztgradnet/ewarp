package erjangx.ewarp.web;

import java.io.PrintWriter;
import java.util.Map;

import erjangx.ewarp.runtime.stats.StatusName;

public class JSONStatsFormatter extends AbstractStatsFormatter {

	public JSONStatsFormatter() {
		super(OutputFormat.JSON, "application/json");
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#startDocument(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	protected void startDocument(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		writer.println("{");
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#endDocument(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	protected void endDocument(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		writer.println("}");
	}
	
	@Override
	protected void doWriteStatusName(PrintWriter writer, String name) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#doWriteValue(java.io.PrintWriter, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void doWriteValue(PrintWriter writer, Object name, Object value) {
		writer.print("\"");
		super.doWriteValue(writer, name, value);
		writer.print("\"");
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#startSection(java.io.PrintWriter, java.lang.String, java.util.Map)
	 */
	@Override
	protected void startSection(PrintWriter writer, String section,
			Map<StatusName, Object> status) {
		writer.print("{");
		writer.print(section);
		writer.println(": ");
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#endSection(java.io.PrintWriter, java.lang.String, java.util.Map)
	 */
	@Override
	protected void endSection(PrintWriter writer, String collectorName,
			Map<StatusName, Object> status) {
		writer.println("}");
	}

}

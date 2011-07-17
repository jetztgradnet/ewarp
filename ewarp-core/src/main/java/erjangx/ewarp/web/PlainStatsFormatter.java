package erjangx.ewarp.web;

import java.io.PrintWriter;
import java.util.Map;

import erjangx.ewarp.runtime.stats.StatusName;

public class PlainStatsFormatter extends AbstractStatsFormatter {

	public PlainStatsFormatter() {
		super(OutputFormat.PLAIN, "text/plain");
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#doWriteStatusName(java.io.PrintWriter, java.lang.String)
	 */
	@Override
	protected void doWriteStatusName(PrintWriter writer, String name) {
		writer.print(name);
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#doWriteValue(java.io.PrintWriter, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void doWriteValue(PrintWriter writer, Object name, Object value) {
		writer.print(": ");
		super.doWriteValue(writer, name, value);
		writer.println();
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#startSection(java.io.PrintWriter, java.lang.String, java.util.Map)
	 */
	@Override
	protected void startSection(PrintWriter writer, String section,
			Map<StatusName, Object> status) {
		writer.print("* ");
		writer.println(section);
	}

}

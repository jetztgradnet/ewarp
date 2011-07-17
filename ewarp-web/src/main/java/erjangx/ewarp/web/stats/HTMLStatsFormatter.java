package erjangx.ewarp.web.stats;

import java.io.PrintWriter;
import java.util.Map;

import erjangx.ewarp.runtime.stats.StatusName;

public class HTMLStatsFormatter extends AbstractStatsFormatter {

	public HTMLStatsFormatter() {
		super(OutputFormat.HTML, "text/html");
	}

	@Override
	protected void doWriteStatusName(PrintWriter writer, String name) {
		String escapedName = escapeText(name);
		writer.print("<h3><a name=\"");
		writer.print(escapedName);
		writer.print("\">");
		writer.print(escapedName);
		writer.print("</h3>");
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#startSection(java.io.PrintWriter, java.lang.String, java.util.Map)
	 */
	@Override
	protected void startSection(PrintWriter writer, String section,
			Map<StatusName, Object> status) {
		writer.print("<h2><a name=\"");
		writer.print(section);
		writer.print("\">");
		writer.print(section);
		writer.print("</a></h2>");
		writer.println("<p>");
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#startCompoundValue(java.io.PrintWriter, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void startCompoundValue(PrintWriter writer, Object name,
			Object value) {
		// TODO Auto-generated method stub
		super.startCompoundValue(writer, name, value);
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#endCompoundValue(java.io.PrintWriter, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void endCompoundValue(PrintWriter writer, Object name,
			Object value) {
		// TODO Auto-generated method stub
		super.endCompoundValue(writer, name, value);
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#startDocument(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	protected void startDocument(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		writer.println("<html>");
		writer.print("<head><title>");
		writer.print("Status");
		writer.print("</title></head>");
		writer.println("<body>");
		if (statuses.size() > 1) {
			writer.println("<h1><a name=\"top\">Status</a></h1>");
		}
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.web.AbstractStatsFormatter#endDocument(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	protected void endDocument(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		writer.println("</body>");
		writer.println("</html>");
	}
}

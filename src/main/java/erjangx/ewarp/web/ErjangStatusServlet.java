package erjangx.ewarp.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erjangx.ewarp.runtime.ErjangRuntime;
import erjangx.ewarp.runtime.stats.InfoCollector;
import erjangx.ewarp.runtime.stats.ModuleCollector;
import erjangx.ewarp.runtime.stats.ProcessCollector;
import erjangx.ewarp.runtime.stats.StatusCollector;
import erjangx.ewarp.runtime.stats.StatusName;

public class ErjangStatusServlet extends ErjangBaseServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			ErjangRuntime runtime = getRuntime();
		
			// get collectors to query
			List<StatusCollector> collectors = getCollectors(request);
			if (collectors.size() == 0) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			else {
				Map<String, Map<StatusName, Object>> statuses = new HashMap<String, Map<StatusName, Object>>();
				for (StatusCollector collector : collectors) {
					Map<StatusName, Object> status = new HashMap<StatusName, Object>();
					if (collector.collectStatus(runtime, status)) {
						statuses.put(collector.getType(), status);
					}
					else {
						log.warn("failed to get status from collector " + collector.getName());
					}
				}
				if (statuses.size() == 0) {
					response.sendError(HttpServletResponse.SC_NO_CONTENT);
				}
				else {
					// get output format
					OutputFormat format = getOutputFormat(request);
					switch (format) {
					case PLAIN:
						response.setContentType("text/plain");
						writePlainOutput(writer, statuses); 
						break;
					default:
					case HTML:
						response.setContentType("text/html");
						writeHTMLOutput(writer, statuses); 
						break;
					case XML:
						response.setContentType("text/xml");
						writeXMLOutput(writer, statuses); 
						break;
					case JSON:
						response.setContentType("application/json");
						writeJSONOutput(writer, statuses); 
						break;
					}
					
				}
			}
		}
		catch (Throwable t) {
			handleError(response, HttpServletResponse.SC_BAD_REQUEST, "failed to get status", t, writer);
		}
		finally {
			closeQuietly(writer);
		}
	}

	private void writePlainOutput(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		if (statuses.size() > 1) {
			writer.println("Status");
			writer.println();
		}
		
		for (Map.Entry<String, Map<StatusName, Object>> entry : statuses.entrySet()) {
			String collectorName = entry.getKey();
			Map<StatusName, Object> status = entry.getValue();
			writePlainOutput(writer, collectorName, status);
		}
	}

	protected void writePlainOutput(PrintWriter writer,
			String collectorName, Map<StatusName, Object> status) {
		writer.println(collectorName);
		for (Map.Entry<StatusName, Object> entry : status.entrySet()) {
			StatusName name = entry.getKey();
			writer.print(name.getShortName());
			writer.print(": ");
			Object value = entry.getValue();
			writer.println(value);
		}
		writer.println();
	}

	protected void writeJSONOutput(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		for (Map.Entry<String, Map<StatusName, Object>> entry : statuses.entrySet()) {
			String collectorName = entry.getKey();
			Map<StatusName, Object> status = entry.getValue();
			writeJSONOutput(writer, collectorName, status);
		}
	}
	
	protected void writeJSONOutput(PrintWriter writer,
			String collectorName, Map<StatusName, Object> status) {
		// TODO Auto-generated method stub
		writer.println("// not yet implemented");
	}

	protected void writeXMLOutput(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		for (Map.Entry<String, Map<StatusName, Object>> entry : statuses.entrySet()) {
			String collectorName = entry.getKey();
			Map<StatusName, Object> status = entry.getValue();
			writeXMLOutput(writer, collectorName, status);
		}
	}

	protected void writeXMLOutput(PrintWriter writer,
			String collectorName, Map<StatusName, Object> status) {
		writer.print("<collector name=\"");
		// TODO escape name
		writer.print(collectorName);
		writer.println("\">");
		
		for (Map.Entry<StatusName, Object> entry : status.entrySet()) {
			writer.print("<status name=\"");
			StatusName name = entry.getKey();
			// TODO escape name
			writer.print(name.getShortName());
			writer.print("\" value=\"");
			Object value = entry.getValue();
			// TODO escape value
			writer.print(value);
			writer.println("\"/>");
		}
		
		writer.println("</collector>");

	}

	protected void writeHTMLOutput(PrintWriter writer,
			Map<String, Map<StatusName, Object>> statuses) {
		writer.println("<html>");
		writer.print("<head><title>");
		writer.print("Status");
		writer.print("</title></head>");
		writer.println("<body>");
		if (statuses.size() > 1) {
			writer.println("<h1><a name=\"top\">Status</a></h1>");
		}
		
		for (Map.Entry<String, Map<StatusName, Object>> entry : statuses.entrySet()) {
			String collectorName = entry.getKey();
			Map<StatusName, Object> status = entry.getValue();
			writeHTMLOutput(writer, collectorName, status);
		}
		writer.println("</body>");
		writer.println("</html>");
	}

	protected void writeHTMLOutput(PrintWriter writer,
			String collectorName, Map<StatusName, Object> status) {
		writer.print("<h2><a name=\"");
		writer.print(collectorName);
		writer.print("\">");
		writer.print(collectorName);
		writer.print("</a></h2>");
		writer.println("<p>");
		
		writer.println("<table>");
		
		for (Map.Entry<StatusName, Object> entry : status.entrySet()) {
			writer.println("<tr>");
			writer.print("<td>");
			StatusName name = entry.getKey();
			writer.print(name.getShortName());
			writer.println("</td>");
			writer.print("<td>");
			Object value = entry.getValue();
			writer.print(value);
			writer.println("</td>");
			writer.println("</tr>");
		}
		
		writer.println("</table>");
		
		writer.println("</p>");
	}

	protected List<StatusCollector> getCollectors(HttpServletRequest request) {
		List<StatusCollector> collectors = new ArrayList<StatusCollector>();
		
		// determine collectors from URI
		String path = request.getPathInfo();
		
		String module = path;
		while (module.startsWith("/")) {
			module = module.substring(1);
		}
		
		if (module.startsWith("processes")) {
			collectors.add(new ProcessCollector());
		}
		else if (module.startsWith("modules")) {
			collectors.add(new ModuleCollector());
		}
		else if (module.startsWith("info")) {
			collectors.add(new InfoCollector());
		}
		
		if ((module == null)
			|| (module.length() == 0)) {
			// add default collectors
			collectors.add(new InfoCollector());
			collectors.add(new ModuleCollector());
			collectors.add(new ProcessCollector());
		}
		
		return collectors;
	}
}

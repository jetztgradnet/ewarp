package erjangx.ewarp.web.stats;

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
import erjangx.ewarp.runtime.stats.NodeCollector;
import erjangx.ewarp.runtime.stats.ProcessCollector;
import erjangx.ewarp.runtime.stats.StatusCollector;
import erjangx.ewarp.runtime.stats.StatusName;
import erjangx.ewarp.web.ErjangBaseServlet;

public class ErjangStatusServlet extends ErjangBaseServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
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
					writeOutput(request, response, statuses); 
				}
			}
		}
		catch (Throwable t) {
			handleError(response, HttpServletResponse.SC_BAD_REQUEST, "failed to get status", t, null);
		}
	}

	protected StatsFormatter getFormatter(OutputFormat format) {
		switch (format) {
		case PLAIN:
			return new PlainStatsFormatter();
		default:
		case HTML:
			return new HTMLStatsFormatter();
		case XML:
			return new XMLStatsFormatter();
		case JSON:
			return new JSONStatsFormatter();
		}
	}
	
	protected void writeOutput(HttpServletRequest request, HttpServletResponse response,
			Map<String, Map<StatusName, Object>> statuses) throws IOException {
		PrintWriter writer = null;
		try {
			// get output format
			OutputFormat format = getOutputFormat(request);
			// get formatter for output format
			StatsFormatter formatter = getFormatter(format);
			String contentType = formatter.getContentType();
			if (contentType != null) {
				response.setContentType(contentType);
			}
			
			writer = response.getWriter();
			formatter.writeOutput(writer, statuses);
		}
		finally {
			closeQuietly(writer);
		}
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
		else if (module.startsWith("nodes")) {
			collectors.add(new NodeCollector());
		}
		
		if ((module == null)
			|| (module.length() == 0)) {
			// add default collectors
			collectors.add(new InfoCollector());
			collectors.add(new ModuleCollector());
			collectors.add(new ProcessCollector());
			collectors.add(new NodeCollector());
		}
		
		return collectors;
	}
}

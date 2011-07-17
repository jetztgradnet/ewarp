package erjangx.ewarp.web;

import java.io.Closeable;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erjangx.ewarp.runtime.ErjangRuntime;
import erjangx.ewarp.web.stats.OutputFormat;

public abstract class ErjangBaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected final static Log log = LogFactory.getLog(ErjangBaseServlet.class);
	
	protected ErjangRuntime getRuntime() {
		ErjangRuntime runtime = ErjangWebRuntime.getErjangRuntime(getServletContext());
		return runtime;
	}
	
	protected void handleError(HttpServletResponse response, int httpError, String message, Throwable error, Closeable output) throws IOException {
		handleError(response, httpError, message, message, error, output);
	}
	
	protected void handleError(HttpServletResponse response, int httpError, String logMessage, String responseMessage, Throwable error, Closeable output) throws IOException {
		// format log message
		StringBuilder builder = new StringBuilder();
		if (logMessage != null) {
			builder.append(logMessage);
		}
		if (error != null) {
			if (builder.length() > 0) {
				builder.append(": ");
			}
			builder.append(error.getMessage());
		}
		if (builder.length() > 0) {
			log.error(builder.toString());
		}
		if (error != null) {
			log.debug("details: ", error);
		}
		
		// close output
		if (output != null) {
			closeQuietly(output);
		}
		
		// send reponse
		if ((httpError > 0)
			&& (response != null)
			&& !response.isCommitted()) {
			if (responseMessage != null) {
				response.sendError(httpError, responseMessage);
			}
			else {
				response.sendError(httpError);
			}
		}
	}
	
	protected OutputFormat getOutputFormat(HttpServletRequest request) {
		// check "file extension" of URL
		String uri = request.getRequestURI();
		if (uri.endsWith(".html")) {
			return OutputFormat.HTML;
		}
		else if (uri.endsWith(".xml")) {
			return OutputFormat.XML;
		}
		else if (uri.endsWith(".json")) {
			return OutputFormat.JSON;
		}
		else if (uri.endsWith(".txt")) {
			return OutputFormat.PLAIN;
		}
		
		// TODO check accepted formats header
		
		// default value: return HTML
		return OutputFormat.HTML;
	}
	
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			}
			catch (Throwable t) {
				// ignore
			}
		}
	}
}

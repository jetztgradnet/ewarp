package erjangx.ewarp.web;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * This {@link Servlet} can be used to start a Erjang runtime.
 * 
 * TODO enable some get/post actions for lifecycle management and
 * information retrieval.
 * 
 * @author wolfgang
 */
public class ErjangServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		// TODO get additional parameters from ServletConfig
		WebHelper helper = new WebHelper(context);
		
		helper.start();
	}
	
	@Override
	public void destroy() {
		ServletContext context = getServletContext();
		WebHelper helper = new WebHelper(context);
		
		helper.shutdown();
		
		super.destroy();
	}
}

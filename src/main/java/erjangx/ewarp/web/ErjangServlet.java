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

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		// get parameters from ServletConfig and ServletContext
		ErjangWebRuntime runtime = ErjangWebRuntime.getErjangRuntime(context);
		if (runtime == null) {
			runtime = new ErjangWebRuntime(config, context);
		}
		
		runtime.start();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		ServletContext context = getServletContext();
		ErjangWebRuntime runtime = ErjangWebRuntime.getErjangRuntime(context);
		if (runtime != null) {
			runtime.shutdown();
		}
		
		super.destroy();
	}
}

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
	
	public final static String ATTRIBUTE_INITIALZED_BY_SERVLET = "erjangx.ewarp.web.INITIALZED_BY_SERVLET";

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
			context.setAttribute(ATTRIBUTE_INITIALZED_BY_SERVLET, getServletName());
		}
		
		runtime.start();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		ServletContext context = getServletContext();
		
		// determine whether the Erjang runtime was initialized by this servlet
		Object name = context.getAttribute(ATTRIBUTE_INITIALZED_BY_SERVLET);
		if ((name != null)
			&& getServletName().equals(name)) {
			// Erjang runtime was initialized by this servlet, so we shut it down
			ErjangWebRuntime runtime = ErjangWebRuntime.getErjangRuntime(context);
			if (runtime != null) {
				runtime.shutdown();
				context.removeAttribute(ATTRIBUTE_INITIALZED_BY_SERVLET);
			}
		}
		
		super.destroy();
	}
}

package erjangx.ewarp.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This {@link ServletContextListener} can be used to start a Erjang runtime.
 * 
 * @author wolfgang
 */
public class ErjangContextListener implements ServletContextListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		// get parameters from ServletConfig and ServletContext
		ErjangWebRuntime runtime = ErjangWebRuntime.getErjangRuntime(context);
		if (runtime == null) {
			runtime = new ErjangWebRuntime(context);
		}
		
		runtime.start();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		ErjangWebRuntime runtime = ErjangWebRuntime.getErjangRuntime(context);
		if (runtime != null) {
			runtime.shutdown();
		}
	}
}

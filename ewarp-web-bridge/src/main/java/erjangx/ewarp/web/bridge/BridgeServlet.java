package erjangx.ewarp.web.bridge;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erjang.EObject;

public class BridgeServlet extends HttpServlet {
	private static final long serialVersionUID = 5528845493205452618L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// TODO init
		// start up and connect to servlet_bridge Erlang application
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		BridgeHandler handler = createHandler(req, resp);
		
		// forward to process servlet_bridge in Erjang runtime
		handler.handleAndRespond();
	}

	/**
	 * Create and configure bridge handler for the current request.
	 * The handler is configured using {@link #configure(BridgeHandler)}, 
	 * any customization can be performed by overriding that method.
	 * 
	 * @param request client request
	 * @param response client response
	 * 
	 * @return bridge handler
	 */
	protected BridgeHandler createHandler(HttpServletRequest request,
			HttpServletResponse response) {
		BridgeHandler handler = new BridgeHandler(request, response);
		configure(handler);
		
		return handler;
	}

	/**
	 * Configure bridge request handler using servlet args.
	 * 
	 * @param handler
	 */
	protected void configure(BridgeHandler handler) {
		// TODO set timeout
		long timeout = 0;
		handler.setTimeout(timeout);
	}
}

package erjangx.ewarp.web.bridge;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BridgeServletResponse extends HttpServletResponseWrapper {

	private final BridgeHandler handlerInfo;

	public BridgeServletResponse(BridgeHandler handlerInfo, HttpServletResponse response) {
		super(response);
		this.handlerInfo = handlerInfo;
	}
	
	public BridgeHandler getHandlerInfo() {
		return handlerInfo;
	}
	
	public HttpServletResponse getHttpServletResponse() {
		return (HttpServletResponse) getResponse();
	}
}

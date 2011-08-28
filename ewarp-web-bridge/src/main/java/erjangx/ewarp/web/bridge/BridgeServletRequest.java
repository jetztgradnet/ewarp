package erjangx.ewarp.web.bridge;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BridgeServletRequest extends HttpServletRequestWrapper {

	private final BridgeHandler handlerInfo;

	public BridgeServletRequest(BridgeHandler handlerInfo, HttpServletRequest request) {
		super(request);
		this.handlerInfo = handlerInfo;
	}
	
	public BridgeHandler getHandlerInfo() {
		return handlerInfo;
	}
	
	public HttpServletRequest getHttpServletResponse() {
		return (HttpServletRequest) getRequest();
	}
}

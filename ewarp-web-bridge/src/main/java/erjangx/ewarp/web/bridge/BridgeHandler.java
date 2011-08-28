package erjangx.ewarp.web.bridge;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erjang.EAtom;
import erjang.EList;
import erjang.EObject;
import erjang.EProc;
import erjang.ERT;
import erjang.ESeq;
import erjang.m.erlang.ErlConvert;
import erjang.m.java.JavaObject;
import erjang.m.rpc.MBox;

public class BridgeHandler {
	// from class RPC, but the constants are not public
	public static EAtom am_rpc = EAtom.intern("rpc");
	public static EAtom am_call_from_java = EAtom.intern("call_from_java");
	public static EAtom am_dummy = EAtom.intern("dummy");
	public static final EAtom am_servlet_bridge = EAtom.intern("servlet_bridge");
	public static final EAtom am_handle_request = EAtom.intern("handle_request");
	
	private MBox mbox;
	private EProc process;
	private BridgeServletRequest request;
	private BridgeServletResponse response;
	private long timeout = 0;
	
	public BridgeHandler(HttpServletRequest req, HttpServletResponse resp) {
		mbox = new MBox();
		request = new BridgeServletRequest(this, req);
		response = new BridgeServletResponse(this, resp);
	}
	
	/**
	 * Handle request. The request is forwarded to the Erjang runtime,
	 * where it will be handled by simple_bridge.
	 * 
	 * @return result object
	 */
	public EObject handle() {
		EAtom method = getMethodAtom(request.getMethod());
		
		// create seq with two placeholders (dummy) for request and response objects
		ESeq args = EList.fromArray(new EObject[] { method, am_dummy, am_dummy });
		
		ESeq callargs = EList.make(am_servlet_bridge, am_handle_request, args, mbox);
		process = new EProc(erjang.m.rpc.Native.get_local_group_leader(), am_rpc, am_call_from_java, callargs);
		// overwrite arg2 with list containing JavaObjects wrapping request and response, as JavaObject wrapper needs a valid process
		process.arg2 = EList.fromArray(new EObject[] { method, JavaObject.box(process, request), JavaObject.box(process, response) });
		
		ERT.run(process);
		
		//process.joinb(timeout);

		// result should be IO list to send back to client
		EObject result = mbox.get_b(timeout);
		
		return result;
	}
	
	/**
	 * Handle request and send response. This is basically
	 * the union of calling {@link #handle()} and {@link #respond(EObject)}.
	 * 
	 * @throws IOException in case of io errors
	 */
	public void handleAndRespond() throws IOException {
		EObject result = handle();
		respond(result);
	}
	
	/**
	 * Send the result object (which might be a iolist) back to the client
	 * @param result result object, e.g. iolist
	 * @throws IOException in case of io errors
	 */
	public void respond(EObject result) throws IOException {
		sendResponse(response, result);
	}
	
	/**
	 * Send response back to the client.
	 * 	
	 * @param response servlet response
	 * @param result result object, which should be an IO list
	 */
	public void sendResponse(HttpServletResponse response, EObject result) throws IOException {		
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			sendResponse(response, out, result);	
		}
		catch (Throwable t) {
			if (out != null) {
				try {
					out.close();
				}
				catch (Throwable t2) {
					// ignore
				}
			}
		}
	}
	
	/**
	 * Send response object back to the client.
	 * 
	 * @param response servlet response object
	 * @param out output stream, does not need to be closed
	 * @param object object to send to the client
	 * 
	 * @throws IOException in case of io errors
	 */
	protected void sendResponse(HttpServletResponse response, OutputStream out, EObject object) throws IOException {
		if (object instanceof JavaObject) {
			JavaObject javaObject = (JavaObject) object;
			Object obj = javaObject.realObject();
			if (object != null) {
				sendResponse(response, out, obj);
			}
		}
		else {
			// use existing io list handling
			ErlConvert.collectList(object, out);
		}
	}

	protected void sendResponse(HttpServletResponse response, OutputStream out, Object object) throws IOException {
		// TODO send to client
	}

	protected EAtom getMethodAtom(String method) {
		// TODO optimize by looking up atoms in a Map? This is probably what EAtom.intern() does anyway...
		return EAtom.intern(method.toUpperCase());
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
}

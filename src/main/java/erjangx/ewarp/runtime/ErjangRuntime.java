package erjangx.ewarp.runtime;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erjang.EAtom;
import erjang.ERT;
import erjang.RPC;
import erjangx.ewarp.cluster.NodeFinder;
import erjangx.ewarp.cluster.NodeListener;
import erjangx.ewarp.cluster.NodeManager;

/**
 * Wrapper for Erjang runtime. Performs initialization and manages
 * the lifecycle. A {@link LifecycleInterceptor} can be configured
 * in order to react on lifecycle messages and perform extended
 * configuration.
 * 
 * @author wolfgang
 */
public class ErjangRuntime implements NodeListener, Settings {
	private final static Log log = LogFactory.getLog(ErjangRuntime.class);
	
	public final static EAtom am_erlang = EAtom.intern("erlang");
	public final static EAtom am_net_adm = EAtom.intern("net_adm");
	public final static EAtom am_net_kernel = EAtom.intern("net_kernel");
	public final static EAtom am_ping = EAtom.intern("ping");
	public final static EAtom am_connect = EAtom.intern("connect_node");
	public final static EAtom am_disconnect = EAtom.intern("disconnect_node");

	private final Object locker = new Object();
	private final NodeManager nodeManager = new NodeManager();
	private volatile Thread erjangThread = null;
	private LifecycleInterceptor interceptor = new BasicLifecycleInterceptor();
	
	public NodeManager getNodeManager() {
		return nodeManager;
	}

	protected void runErjang() {
		// TODO get some args from config, find automatically
		String[] ARGS = {
				"-progname", "ej",
				// don't open a shell, as Erlang terminates, when stdin is closed
				"-noshell",
				"-home", System.getProperty("user.home"),
				//"-root", "/Users/krab/Projects/OTP_R13B04",
				// OTP is available on classpath
				"+A", "2",
				"+S", "1",
				// TODO use erjang.erts.version property
				"+e", "5.7.5",
				"-s", "rpc", "erjang_started"
			};
			
		try {
			log.info("starting Erjang runtime");
			erjang.Main.main(ARGS);
		} catch (Exception e) {
			log.warn("failed to start Erjang runtime: " + e.getMessage());
			log.debug("details: ", e);
			// re-throw exception
			throw new RuntimeException("failed to start Erjang runtime", e);
		}
	}
	
	public boolean isRunning() {
		synchronized(locker) {
			return (erjangThread != null);
		}
	}
	
	public void init(Properties props) {
		// TODO look for property ewarp.config.url and load additional properties
		String url = props.getProperty(EWARP_CONFIG_URL);
		if (url != null) {
			InputStream in = null;
			try {
				// TODO consider other loading options, e.g. through WebHelper using ServletContext
				in = getClass().getResourceAsStream(url);
				Properties externalProps = new Properties();
				externalProps.load(in);
			}
			catch (Throwable t) {
				log.warn("failed to load external runtime configuration from " + url + ": " + t.getMessage());
				// TODO check another property, whether to bail out, 
				// if external configuration can not be found
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (Throwable t) {
						// ignore
					}
				}
			}
		}
		
		createLifecycleInterceptor(props);
		
		// log configuration
		if (log.isDebugEnabled()) {
			List<String> params = new ArrayList<String>();
			for (Object key : props.keySet()) {
				params.add(key.toString());
			}
			if (params.size() > 0) {
				Collections.sort(params);
				log.debug("=============== configuration ==============");
				for (String name : params) {
					String value = props.getProperty(name);
					log.debug(name + "=" + value);
				}
				log.debug("============================================");
			}
		}
		
		// TODO init from properties
		
	}
	
	protected void createLifecycleInterceptor(Properties props) {
		LifecycleInterceptor interceptor = null;
		String interceptorClassName = props.getProperty(Settings.EWARP_LIFECYCLE_INTERCEPTOR);
		
		if (interceptorClassName != null) {
			try {
				@SuppressWarnings("unchecked")
				Class<LifecycleInterceptor> interceptorClass = (Class<LifecycleInterceptor>) (getClass().getClassLoader().loadClass(interceptorClassName));
				interceptor = interceptorClass.newInstance();
				interceptor.init(this, props);
			}
			catch (Exception e) {
				log.error("failed to create LifecycleInterceptor from " + interceptorClassName + ": " + e.getMessage());
				log.debug("details: ", e);
				
				// re-throw exception
				throw new RuntimeException(e);
			}
		}
		
		if (interceptor != null) {
			// replace existing interceptor
			this.interceptor = interceptor;
		}
		
	}

	public void start() {
		interceptor.preStart();
		
		// register lifecycle interceptor as node listener
		if (interceptor instanceof NodeListener) {
			NodeListener nodeListener = (NodeListener) interceptor;
			nodeManager.addNodeListener(nodeListener);
		}
		
		synchronized(locker) {
			if (erjangThread != null) {
				return;
			}
			erjangThread = new Thread() {
				public void run() {
					runErjang();
				};
			};
			erjangThread.setDaemon(true);
			erjangThread.start();

			//
			//  The   -s rpc erjang_started    argument makes the loader call rpc:erjang_started/0
			//  which is the trigger that will release 'wait_for_erjang_started'
			//
			log.debug("waiting for Erjang runtime");
			RPC.wait_for_erjang_started(60*1000L);
		}
		
		interceptor.postStart();
	}
	
	public void shutdown() {
		interceptor.preShutdown();
		
		// register lifecycle interceptor as node listener
		if (interceptor instanceof NodeListener) {
			NodeListener nodeListener = (NodeListener) interceptor;
			nodeManager.removeNodeListener(nodeListener);
		}
		
		try {
			synchronized(locker) {
				if (erjangThread == null) {
					// not currently running
					return;
				}
				
				try {
					log.info("shutting down Erjang runtime");
					ERT.shutdown();
					
					erjangThread.join();
				}
				catch (Throwable e) {
					log.error("failed to wait for Erjang runtime to shut down: " + e.getMessage());
					log.debug("details: ", e);
					
					// re-throw exception
					throw new RuntimeException(e);
				}
				finally {			
					erjangThread = null;
				}
			}
		}
		finally {
			interceptor.postShutdown();
		}
	}

	public void nodeAdded(NodeFinder finder, String nodeName) {
		synchronized(locker) {
			if (!isRunning()) {
				return;
			}
			EAtom am_nodeName = EAtom.intern(nodeName);
			//RPC.call(am_net_adm, am_ping, am_nodeName);
			RPC.call(am_net_kernel, am_connect, am_nodeName);
		}
	}

	public void nodeRemoved(NodeFinder finder, String nodeName) {
		synchronized(locker) {
			if (!isRunning()) {
				return;
			}
			EAtom am_nodeName = EAtom.intern(nodeName);
			
			// TODO how to disconnect node?
			RPC.call(am_erlang, am_disconnect, am_nodeName);
		}
	}

}

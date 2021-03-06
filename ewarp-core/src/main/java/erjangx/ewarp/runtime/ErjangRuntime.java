package erjangx.ewarp.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import erjangx.ewarp.cluster.NodeNameResolver;
import erjangx.ewarp.util.Util;

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
	private Properties runtimeProps = null;
	private String[] runtimeArgs = null;
	
	public NodeManager getNodeManager() {
		return nodeManager;
	}

	protected void runErjang() {
		try {
			log.info("starting Erjang runtime");
			erjang.Main.main(runtimeArgs);
		} catch (Exception e) {
			log.warn("failed to start Erjang runtime: " + e.getMessage());
			log.debug("details: ", e);
			// re-throw exception
			throw new RuntimeException("failed to start Erjang runtime", e);
		}
	}
	
	protected void configureErjang(Properties props, List<String> args) {
		configureErjangRuntime(props, args);

		// don't open a shell, as Erlang terminates, when stdin is closed
		args.add("-noinput");
		args.add("-noshell");

		configureCluster(props, args);
	}

	protected void configureCluster(Properties props, List<String> args) {
		String nodeName = Util.getCanonicalString(determineNodeName(props));
		if (nodeName == null) {
			return;
		}
		String namePart = null;
		String nodePart = null;
		int pos = nodeName.indexOf("@");
		if (pos >= 0) {
			// have node part
			namePart = nodeName.substring(0, pos);
			nodePart = nodeName.substring(pos + 1);
		}
		else {
			namePart = nodeName;
		}

		if (namePart == null) {
			namePart = ERJANG_DEFAULT_NODENAME;
		}
		
		// determine whether to use long or short name
		Boolean shortName = getRuntimePropertyAsBoolean(props, ERJANG_ARG_USERSHORTNAME, null);
		if (shortName != null) {
			if (shortName.booleanValue()) {
				// short name, remove node part
				nodePart = null;
			}
			else {
				// long name, require nodePart
				if (nodePart == null) {
					nodePart = getLocalHostName();
				}
			}
		}
		else {
			// auto-mode
			if (nodePart == null) {
				nodePart = getLocalHostName();
			}
			// determine whether host name is a full domain name or a simple host name
			shortName = Boolean.valueOf(!nodePart.contains("."));
		}
		
		if (shortName.booleanValue()
			|| (nodePart == null)) {
			// non-qualified host name, use short name
			args.add("-sname");
		}
		else {
			// host name is fully qualified (contains at least one dot)
			args.add("-name");
			// create node name
			nodeName = namePart + "@" + nodePart;
		}
		args.add(nodeName);

		String cookie = Util.getCanonicalString(determineCookie(props));
		if (cookie != null) { 
			args.add("-setcookie");
			args.add(cookie);
		}
	}
	
	protected String getLocalHostName() {
		String nodePart = null;
		InetAddress localAddress;
		try {
			localAddress = InetAddress.getLocalHost();
			nodePart = localAddress.getHostName();
			//nodePart = localAddress.getCanonicalHostName();
		}
		catch (UnknownHostException e) {
			nodePart = "localhost";

			log.error("failed to determine local host name: " + e.getMessage());
			log.debug("details: ", e);
		}
		
		return nodePart;
	}

	protected void configureAndStartNodeFinders(Properties props) {
		// TODO create and configure NodeFinders
	}

	protected String determineCookie(Properties props) {
		String cookie = getRuntimeProperty(props, ERJANG_ARG_COOKIE, null);
		if (cookie == null) {
			cookie = ERJANG_DEFAULT_COOKIE;
		}
		return cookie;
	}

	protected String determineNodeName(Properties props) {
		String nodeName = null;
		NodeNameResolver resolver = getNodeNameResolver(props);
		if (resolver != null) {
			nodeName = resolver.getNodeName();
		}
		if (nodeName == null) {
			// no node name yet, check args
			nodeName = getRuntimeProps().getProperty(ERJANG_ARG_NODENAME);
		}
		if (nodeName == null) {
			log.debug("no NodeNameResolver found, using default node name: " + nodeName);
			nodeName = ERJANG_DEFAULT_NODENAME;
		}
		return nodeName;
	}

	protected NodeNameResolver getNodeNameResolver(Properties props) {
		// TODO determine NodeNameResolver from settings and create an instance
		return null;
	}

	protected void configureErjangRuntime(Properties props, List<String> args) {
		args.add("-progname");
		args.add("ej");

		args.add("-home");
		args.add(System.getProperty("user.home"));

		// use erjang.erts.version property
		String ertsVersion = props.getProperty(Settings.ERJANG_ERTS_VERSION, "5.7.5");
		args.add("+e");
		args.add(ertsVersion);

		// TODO make sure, that OTP is available on classpath
		// otherwise provide root path
		//args.add("-root");
		//args.add("/Users/krab/Projects/OTP_R13B04");

		// size of async thread pool
		args.add("+A");
		args.add("2");

		// size of sync thread pool
		args.add("+S");
		args.add("1");

		// wait for Erjang to be started
		args.add("-s");
		args.add("rpc");
		args.add("erjang_started");

	}

	public boolean isRunning() {
		synchronized(locker) {
			return (erjangThread != null);
		}
	}

	protected InputStream loadResource(String url) throws IOException {
		return getClass().getResourceAsStream(url);
	}

	public void init(Properties props) {
		// look for property ewarp.config.url and load additional properties
		String url = props.getProperty(EWARP_CONFIG_URL);
		if (url != null) {
			InputStream in = null;
			try {
				// TODO consider other loading options, e.g. through WebHelper using ServletContext
				in = loadResource(url);
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

		// give interceptor a change to modify properties
		runtimeProps = interceptor.configure(props);
		
		// log configuration
		if (log.isDebugEnabled()) {
			List<String> params = new ArrayList<String>();
			for (Object key : runtimeProps.keySet()) {
				params.add(key.toString());
			}
			if (params.size() > 0) {
				Collections.sort(params);
				log.debug("=============== configuration ==============");
				for (String name : params) {
					String value = runtimeProps.getProperty(name);
					log.debug(name + "=" + value);
				}
				log.debug("============================================");
			}
		}
		
		// init from properties
		List<String> args = new ArrayList<String>();
		configureErjang(runtimeProps, args);
		runtimeArgs = (String[]) args.toArray(new String[args.size()]);
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
	
	protected String[] getRuntimeArgs() {
		return runtimeArgs;
	}
	
	protected Properties getRuntimeProps() {
		return runtimeProps;
	}
	
	protected boolean hasRuntimeProperty(String name) {
		return hasRuntimeProperty(runtimeProps, name);
	}
	
	protected boolean hasRuntimeProperty(Properties props, String name) {
		return props.containsKey(name);
	}
	
	protected String getRuntimeProperty(String name, String defVal) {
		return getRuntimeProperty(runtimeProps, name, defVal);
	}
	
	protected String getRuntimeProperty(Properties props, String name, String defVal) {
		String value = defVal;
		
		if (props != null) {
			value = props.getProperty(name, defVal);
		}
		
		return value;
	}
	
	protected int getRuntimePropertyAsInt(String name, int defVal) {
		return getRuntimePropertyAsInt(runtimeProps, name, defVal);
	}
	
	protected int getRuntimePropertyAsInt(Properties props, String name, int defVal) {
		int value = defVal;
		
		String v = getRuntimeProperty(props, name, null);
		if (v != null) {
			try {
				value = Integer.parseInt(v);
			}
			catch (Throwable t) {
				// ignore
			}
		}
		
		return value;
	}
	
	protected double getRuntimePropertyAsDouble(String name, double defVal) {
		return getRuntimePropertyAsDouble(runtimeProps, name, defVal);
	}
	
	protected double getRuntimePropertyAsDouble(Properties props, String name, double defVal) {
		double value = defVal;
		
		String v = getRuntimeProperty(props, name, null);
		if (v != null) {
			try {
				value = Double.parseDouble(v);
			}
			catch (Throwable t) {
				// ignore
			}
		}
		
		return value;
	}
	
	protected Boolean getRuntimePropertyAsBoolean(String name, Boolean defVal) {
		return getRuntimePropertyAsBoolean(runtimeProps, name, defVal);
	}
	
	protected Boolean getRuntimePropertyAsBoolean(Properties props, String name, Boolean defVal) {
		Boolean value = defVal;
		
		String v = getRuntimeProperty(props, name, null);
		if (v != null) {
			try {
				value = Boolean.valueOf(v);
			}
			catch (Throwable t) {
				// ignore
			}
		}
		
		return value;
	}
	
	protected boolean getRuntimePropertyAsBoolean(String name, boolean defVal) {
		return getRuntimePropertyAsBoolean(runtimeProps, name, defVal);
	}
	
	protected boolean getRuntimePropertyAsBoolean(Properties props, String name, boolean defVal) {
		boolean value = defVal;
		
		String v = getRuntimeProperty(props, name, null);
		if (v != null) {
			try {
				value = Boolean.parseBoolean(v);
			}
			catch (Throwable t) {
				// ignore
			}
		}
		
		return value;
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
		
		configureAndStartNodeFinders(runtimeProps);
		
		interceptor.postStart();
	}
	
	public void shutdown() {
		interceptor.preShutdown();
		
		// register lifecycle interceptor as node listener
		if (interceptor instanceof NodeListener) {
			NodeListener nodeListener = (NodeListener) interceptor;
			nodeManager.removeNodeListener(nodeListener);
		}
		
		nodeManager.removeAllNodeFinders();
		
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

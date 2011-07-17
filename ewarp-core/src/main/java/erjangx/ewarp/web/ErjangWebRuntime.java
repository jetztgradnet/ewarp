package erjangx.ewarp.web;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import erjangx.ewarp.runtime.ErjangRuntime;

public class ErjangWebRuntime extends ErjangRuntime {
	public final static String ERJANG_RUNTIME = ErjangRuntime.class.getName();
	
	private final ServletContext context;
	private final ServletConfig config;
	
	public ErjangWebRuntime(ServletContext context) {
		this(null, context);
	}
	
	public ErjangWebRuntime(ServletConfig config) {
		this(config, config.getServletContext());
	}
	
	public ErjangWebRuntime(ServletConfig config, ServletContext context) {
		this.config = config;
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.ErjangRuntime#start()
	 */
	@Override
	public void start() {
		ErjangRuntime runtime = getErjangRuntime(context);
		if (runtime == null) {
			// store runtime in servlet context
			context.setAttribute(ERJANG_RUNTIME, this);
		}
		
		if (!isRunning()) {
			// configure runtime
			configureRuntime();
		
			// Erjang runtime is not yet running
			super.start();
		}
	}
	
	/**
	 * Configure {@link ErjangRuntime}. This method extracts configuration parameters from
	 * the servlet context.
	 * 
	 * @param runtime Erjang runtime to configure
	 */
	protected void configureRuntime() {
		Properties contextProps = getConfigurationProperties(context);
		Properties configProps = getConfigurationProperties(config);

		// merge props
		Properties props = new Properties();
		props.putAll(contextProps);
		props.putAll(configProps);
		
		init(props);
		
		// TODO provide additional configuration?
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.ErjangRuntime#shutdown()
	 */
	@Override
	public void shutdown() {
		try {
			super.shutdown();
		}
		finally {
			context.removeAttribute(ERJANG_RUNTIME);
		}
	}
	
	public static ErjangWebRuntime getErjangRuntime(ServletContext context) {
		return (ErjangWebRuntime) context.getAttribute(ERJANG_RUNTIME);
	}
	
	public static boolean hasErjangRuntime(ServletContext context) {
		return (getErjangRuntime(context) != null);
	}
	
	public static Properties getConfigurationProperties(ServletContext context) {
		Properties props = new Properties();
		
		if (context == null) {
			return props;
		}
		
		// get properties from init params
		@SuppressWarnings("unchecked")
		List<String> paramNames = Collections.list(context.getInitParameterNames());
		for (String name : paramNames) {
			// accept only properties starting with ewarp, erjang, or erlang
			if (!name.startsWith("ewarp.")
				&& !name.startsWith("erjang.")
				&& !name.startsWith("erlang.")) {
				continue;
			}
			String value = context.getInitParameter(name);
			props.setProperty(name, value);
		}
		
		
		// TODO determine/guess additional configuration?
		
		return props;
	}
	
	public static Properties getConfigurationProperties(ServletConfig config) {
		Properties props = new Properties();
		
		if (config == null) {
			return props;
		}
		
		// get properties from init params
		@SuppressWarnings("unchecked")
		List<String> paramNames = Collections.list(config.getInitParameterNames());
		for (String name : paramNames) {
			// accept only properties starting with ewarp, erjang, or erlang
			if (!name.startsWith("ewarp.")
				&& !name.startsWith("erjang.")
				&& !name.startsWith("erlang.")) {
				continue;
			}
			String value = config.getInitParameter(name);
			props.setProperty(name, value);
		}
		
		
		// TODO determine/guess additional configuration?
		
		return props;
	}
}

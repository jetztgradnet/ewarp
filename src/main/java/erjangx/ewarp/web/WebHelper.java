package erjangx.ewarp.web;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import erjangx.ewarp.runtime.ErjangRuntime;

public class WebHelper {
	public final static String ERJANG_RUNTIME = ErjangRuntime.class.getName();
	
	private final ServletContext context;
	private final ServletConfig config;
	
	public WebHelper(ServletContext context) {
		this(null, context);
	}
	
	public WebHelper(ServletConfig config) {
		this(config, config.getServletContext());
	}
	
	public WebHelper(ServletConfig config, ServletContext context) {
		this.config = config;
		this.context = context;
	}
	
	public void start() {
		ErjangRuntime runtime = getErjangRuntime(context);
		if (runtime != null) {
			// Erjang runtime is already running
			return;
		}
		// Launch Erjang by running erjang.Main.main(String[]) in a fresh thread
		runtime = new ErjangRuntime();
		
		// configure runtime
		configureRuntime(runtime);
		
		runtime.start();
		
		// store runtime in servlet context
		context.setAttribute(ERJANG_RUNTIME, runtime);
	}
	
	/**
	 * Configure {@link ErjangRuntime}. This method extracts configuration parameters from
	 * the servlet context.
	 * 
	 * @param runtime Erjang runtime to configure
	 */
	protected void configureRuntime(ErjangRuntime runtime) {
		Properties contextProps = getConfigurationProperties(context);
		Properties configProps = getConfigurationProperties(config);

		// merge props
		Properties props = new Properties();
		props.putAll(contextProps);
		props.putAll(configProps);
		
		runtime.init(props);
		
		// TODO provide additional configuration?
	}
	
	public void shutdown() {
		// get runtime from servlet context
		ErjangRuntime runtime = getErjangRuntime(context);
		context.removeAttribute(ERJANG_RUNTIME);
		
		if (runtime != null) {
			runtime.shutdown();
		}
	}
	
	public static ErjangRuntime getErjangRuntime(ServletContext context) {
		return (ErjangRuntime) context.getAttribute(ERJANG_RUNTIME);
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

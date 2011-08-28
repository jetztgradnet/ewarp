package erjangx.ewarp.runtime;

import java.util.Properties;

public interface Settings {
	public final static String PREFIX = "ewarp.";

	/**
	 * URL of {@link Properties} file, which contains additional configuration+
	 * properties. The file is loaded using {@link Properties#load(java.io.InputStream)}.
	 */
	public final static String EWARP_CONFIG_URL = PREFIX + "config.url";

	/**
	 * Class name for {@link LifecycleInterceptor}.
	 */
	public static final String EWARP_LIFECYCLE_INTERCEPTOR = PREFIX + "lifecycle.interceptor";

	/**
	 * ERTS version used by Erjang.
	 */
	public static final String ERJANG_ERTS_VERSION = "erjang.erts.version";

	/**
	 * OTP version used by Erjang.
	 */
	public static final String ERJANG_OTP_VERSION = "erjang.otp.version";
	
	
	/**
	 * Nodename.
	 */
	public static final String ERJANG_ARG_NODENAME = "erjang.nodename";
	
	/**
	 * Default node name (local part) to be used if nothing is specified.
	 */
	public static final String ERJANG_DEFAULT_NODENAME = "node";
	
	/**
	 * Specifies whether to use short name (<code>true</code>) or long name (<code>false</code>)
	 */
	public static final String ERJANG_ARG_USERSHORTNAME = "erjang.shortname";
	
	/**
	 * Cookie.
	 */
	public static final String ERJANG_ARG_COOKIE = "erjang.cookie";
	
	/**
	 * Default cookie: none.
	 */
	public static final String ERJANG_DEFAULT_COOKIE = null;

	
}

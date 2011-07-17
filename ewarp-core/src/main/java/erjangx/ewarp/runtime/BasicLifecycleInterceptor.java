package erjangx.ewarp.runtime;

import java.util.Properties;

/**
 * Basic implementation of a {@link LifecycleInterceptor}, which 
 * stores the {@link ErjangRuntime} for later use. Ignores all
 * lifecycle messages.
 * 
 * @author wolfgang
 */
public class BasicLifecycleInterceptor implements LifecycleInterceptor {

	private ErjangRuntime runtime = null;
	
	/**
	 * Get {@link ErjangRuntime}.
	 * 
	 * @return runtime
	 */
	protected ErjangRuntime getRuntime() {
		return runtime;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.LifecycleInterceptor#init(erjangx.ewarp.runtime.ErjangRuntime, java.util.Properties)
	 */
	public void init(ErjangRuntime runtime, Properties props) {
		this.runtime = runtime;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.LifecycleInterceptor#configure(java.util.Properties)
	 */
	public Properties configure(Properties params) {
		return params;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.LifecycleInterceptor#preStart()
	 */
	public void preStart() {
		// ignored
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.LifecycleInterceptor#postStart()
	 */
	public void postStart() {
		// ignored
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.LifecycleInterceptor#preShutdown()
	 */
	public void preShutdown() {
		// ignored
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.LifecycleInterceptor#postShutdown()
	 */
	public void postShutdown() {
		// ignored
	}

}

package erjangx.ewarp.runtime;

import java.util.Properties;

import erjangx.ewarp.cluster.NodeListener;
import erjangx.ewarp.cluster.NodeManager;

/**
 * Interceptor for lifecycle events. If the implementing class
 * also implements {@link NodeListener}, it is registered with the
 * {@link ErjangRuntime}'s {@link NodeManager}.
 *  
 * @author wolfgang
 */
public interface LifecycleInterceptor {
	void init(ErjangRuntime runtime, Properties props);
	Properties configure(Properties params);
	void preStart();
	void postStart();
	void preShutdown();
	void postShutdown();
}

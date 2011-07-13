package erjangx.ewarp.runtime.stats;

import java.util.Map;

import erjangx.ewarp.runtime.ErjangRuntime;

public interface StatusCollector {
	/**
	 * Get collector name. The name is only used for display purposes
	 * and may contain any charaters.
	 * 
	 * @return collector name, must not be <code>null</code>.
	 */
	String getName();
	
	/**
	 * Get collector type. The type is a short identifier used for internal
	 * purposes, e.g. references, etc. It should not contain whitespace or
	 * special characters, but resemble a valid Java identifier.
	 * 
	 * @return collector type, must not be <code>null</code>.
	 */
	String getType();
	
	boolean collectStatus(ErjangRuntime runtime, Map<StatusName, Object> status);
}

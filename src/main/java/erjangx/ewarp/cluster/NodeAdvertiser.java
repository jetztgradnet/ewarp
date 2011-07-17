package erjangx.ewarp.cluster;

import erjangx.ewarp.runtime.ErjangRuntime;

/**
 * Advertise node name to other nodes, e.g. by posting the 
 * name to a message queue or database.
 * 
 * @author wolfgang
 */
public interface NodeAdvertiser {
	/**
	 * Advertise node name to other nodes, e.g. by posting the 
	 * name to a message queue or database.
	 * 
	 * @param runtime Erjang runtime
	 * @param nodeName node name to advertize. The node name is 
	 * 			in the form 'name@host' or 'name@host.domain'
	 */
	void advertiseNode(ErjangRuntime runtime, String nodeName);
}

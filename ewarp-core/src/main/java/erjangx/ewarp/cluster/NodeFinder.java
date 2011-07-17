package erjangx.ewarp.cluster;

public interface NodeFinder {
	boolean addNodeListener(NodeListener listener);
	boolean removeNodeListener(NodeListener listener);
	void removeAllNodeListeners();
	
	/**
	 * Determine whether this {@link NodeFinder} knows a
	 * specified node.
	 * 
	 * @param nodeName node name to check
	 * 
	 * @return <code>true</code> if the node is known, 
	 * 			<code>false</code> otherwise
	 */
	boolean hasNode(String nodeName);
}

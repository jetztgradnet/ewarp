package erjangx.ewarp.cluster;

/**
 * A NodeNameResolver determines the node name to be used for
 * this node.
 * 
 * @author wolfgang
 */
public interface NodeNameResolver {
	/**
	 * Determine node name to be used for this node.
	 * The node name may either by a short name or full name,
	 * i.e. in the form 'name@host' or 'name@host.domain'.
	 * 
	 * @return node name or null for non-distributed node
	 */
	String getNodeName();
}

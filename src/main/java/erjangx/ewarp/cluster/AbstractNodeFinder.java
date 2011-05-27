package erjangx.ewarp.cluster;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base implementation of a {@link NodeFinder}, which provides caching 
 * and listener management.
 * 
 * @author wolfgang
 */
public abstract class AbstractNodeFinder implements NodeFinder {
	private final Object locker = new Object();
	private final Set<NodeListener> listeners;
	private final Set<String> knownNodes;
	
	public AbstractNodeFinder() {
		listeners = new HashSet<NodeListener>();
		knownNodes = new HashSet<String>();
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.NodeFinder#hasNode(java.lang.String)
	 */
	public boolean hasNode(String nodeName) {
		synchronized (locker) {
			return knownNodes.contains(nodeName);
		}
	}
	
	/**
	 * Get number of known nodes.
	 * 
	 * @return number of known nodes
	 */
	public int size() {
		synchronized (locker) {
			return knownNodes.size();
		}
	}
	
	/**
	 * Synchronize complete set of node names with current cache.
	 * New and removed nodes are signaled to all {@link NodeListener}s. 
	 * 
	 * @param nodeNames set of node names.
	 * 
	 * @return <code>true</code> if at least one node was added or removed,
	 * 			<code>false</code> otherwise
	 */
	protected boolean syncNodes(Set<String> nodeNames) {
		boolean changes = false;
		synchronized (locker) {
			Set<String> removedNodes = new HashSet<String>(knownNodes);
			Set<String> addedNodes = new HashSet<String>(nodeNames);
			
			addedNodes.removeAll(knownNodes);
			removedNodes.removeAll(nodeNames);
			
			for (String nodeName : addedNodes) {
				if (addNode(nodeName)) {
					changes = true;
				}
			}
			
			for (String nodeName : removedNodes) {
				if (removeNode(nodeName)) {
					changes = true;
				}
			}
		}
		
		return changes;
	}
	
	/**
	 * Add a node to the internal cache. If the node is not yet known,
	 * all {@link NodeListener}s are notified.
	 * 
	 * @param nodeName node to add
	 * 
	 * @return <code>true</code> if the node was not yet known,
	 * 			<code>false</code> otherwise
	 */
	protected boolean addNode(String nodeName) {
		if (nodeName == null) {
			return false;
		}
		nodeName = nodeName.trim();
		if (nodeName.length() == 0) {
			return false;
		}
		boolean added = false;
		synchronized (locker) {
			added = knownNodes.add(nodeName);
		}
		
		if (added) {
			// notify listeners
			fireNodeAdded(nodeName);
		}
	
		return added;
	}
	
	/**
	 * Remove a node from the internal cache. If the node was known and removed,
	 * all {@link NodeListener}s are notified.
	 * 
	 * @param nodeName node to remove
	 * 
	 * @return <code>true</code> if the node was known and removed,
	 * 			<code>false</code> otherwise
	 */
	protected boolean removeNode(String nodeName) {
		boolean removed = false;
		synchronized (locker) {
			removed = knownNodes.remove(nodeName);
		}
		
		if (removed) {
			// notify listeners
			fireNodeRemoved(nodeName);
		}
	
		return removed;
	}

	/**
	 * Remove all known nodes from internal cache and notify all
	 * {@link NodeListener}s.
	 * 
	 * @return <code>true</code> if at least one node was removed,
	 * 			<code>false</code> otherwise
	 */
	protected boolean removeAllNodes() {
		Set<String> nodeNames = Collections.emptySet();
		synchronized (locker) {
			if (knownNodes.size() > 0) {
				// clone set
				nodeNames = new HashSet<String>(knownNodes);
				knownNodes.clear();
			}
		}
		
		// notify listeners
		for (String nodeName : nodeNames) {
			fireNodeRemoved(nodeName);
		}
		
		return nodeNames.size() > 0;
	}
	
	/**
	 * Get currently known node names.
	 * 
	 * @return set of currently known node names
	 */
	public Set<String> getKnownNodes() {
		return knownNodes;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.NodeFinder#addNodeListener(erjangx.ewarp.cluster.NodeListener)
	 */
	public boolean addNodeListener(NodeListener listener) {
		synchronized(locker) {
			if (listeners.add(listener)) {
				// notify listener of all known nodes
				for (String nodeName : knownNodes) {
					listener.nodeAdded(this, nodeName);
				}
				
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.NodeFinder#removeNodeListener(erjangx.ewarp.cluster.NodeListener)
	 */
	public boolean removeNodeListener(NodeListener listener) {
		synchronized(locker) {
			if (listeners.remove(listener)) {
				// notify listener that all nods are removed
				for (String nodeName : knownNodes) {
					listener.nodeRemoved(this, nodeName);
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.NodeFinder#removeAllNodeListeners()
	 */
	public void removeAllNodeListeners() {
		synchronized(locker) {
			for (NodeListener listener : listeners) {
				// notify listener that all nods are removed
				for (String nodeName : knownNodes) {
					listener.nodeRemoved(this, nodeName);
				}
			}
			
			listeners.clear();
		}
	}
	
	/**
	 * Fire a nodeAdded event and notify each listener.
	 * 
	 * @param nodeName name of added node
	 */
	protected void fireNodeAdded(String nodeName) {
		synchronized(locker) {
			for (NodeListener listener : listeners) {
				listener.nodeAdded(this, nodeName);
			}
		}
	}
	
	/**
	 * Fire a nodeRemoved event and notify each listener.
	 * 
	 * @param nodeName name of removed node
	 * @param nodeName
	 */
	protected void fireNodeRemoved(String nodeName) {
		synchronized(locker) {
			for (NodeListener listener : listeners) {
				listener.nodeRemoved(this, nodeName);
			}
		}
	}
	
}
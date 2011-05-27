package erjangx.ewarp.cluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeManager extends AbstractNodeFinder implements NodeFinder, NodeListener {
	private final Object locker = new Object();
	private final Map<NodeFinder, Set<String> > nodeFinders;
	
	public NodeManager() {
		nodeFinders = new HashMap<NodeFinder, Set<String>>();
	}
	
	public boolean addNodeFinder(NodeFinder nodeFinder) {
		synchronized(locker) {
			if (!nodeFinders.containsKey(nodeFinder)) {
				nodeFinders.put(nodeFinder, new HashSet<String>());
				
				// register our NodeListener
				nodeFinder.addNodeListener(this);
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean removeNodeFinder(NodeFinder nodeFinder) {
		synchronized(locker) {
			if (nodeFinders.containsKey(nodeFinder)) {
				nodeFinder.removeNodeListener(this);
				nodeFinders.remove(nodeFinder);
			
				// TODO rebuild aggregated set of node names
				
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.NodeListener#nodeAdded(erjangx.ewarp.cluster.NodeFinder, java.lang.String)
	 */
	public void nodeAdded(NodeFinder finder, String nodeName) {
		synchronized(locker) {
			// get node list for current finder
			Set<String> nodeList = nodeFinders.get(finder);
			if (nodeList == null) {
				// unknown finder, Ignore
				return;
			}
		
			nodeList.add(nodeName);
		}
		addNode(nodeName);
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.NodeListener#nodeRemoved(erjangx.ewarp.cluster.NodeFinder, java.lang.String)
	 */
	public void nodeRemoved(NodeFinder finder, String nodeName) {
		boolean remove = true;
		synchronized(locker) {
			// get node list for current finder
			Set<String> nodeList = nodeFinders.get(finder);
			if (nodeList == null) {
				// unknown finder, Ignore
				return;
			}
			
			if (nodeList.remove(nodeName)) {
				// remove node only, if it is not known by any other NodeFinders
				for (NodeFinder checkFinder : nodeFinders.keySet()) {
					if (checkFinder == finder) {
						continue;
					}
					if (checkFinder.hasNode(nodeName)) {
						// node is also known to some other NodeFinder, so we keep it
						remove = false;
						break;
					}
				}
			}
		}
		
		if (remove) {
			removeNode(nodeName);
		}
	}
}

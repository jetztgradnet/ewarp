package erjangx.ewarp.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link NodeListener} implementation, which records node added/removed events in a list.
 * 
 * @author wolfgang
 */
public class RecordingNodeListener implements NodeListener {
	private final List<String> events;

	RecordingNodeListener() {
		this.events = new ArrayList<String>();
	}
	
	public void clear() {
		events.clear();
	}
	
	public List<String> getEvents() {
		return events;
	}

	public void nodeRemoved(NodeFinder finder, String nodeName) {
		events.add("removed:" + nodeName);
	}

	public void nodeAdded(NodeFinder finder, String nodeName) {
		events.add("added:" + nodeName);
	}
}

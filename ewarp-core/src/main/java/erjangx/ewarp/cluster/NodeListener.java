package erjangx.ewarp.cluster;

public interface NodeListener {
	void nodeAdded(NodeFinder finder, String nodeName);
	void nodeRemoved(NodeFinder finder, String nodeName);
}

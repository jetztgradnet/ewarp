package erjangx.ewarp.cluster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

public class StaticNodeFinder extends AbstractNodeFinder {
	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.AbstractNodeFinder#syncNodes(java.util.Set)
	 */
	@Override
	public boolean syncNodes(Set<String> nodeNames) {
		return super.syncNodes(nodeNames);
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.AbstractNodeFinder#addNode(java.lang.String)
	 */
	@Override
	public boolean addNode(String nodeName) {
		return super.addNode(nodeName);
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.AbstractNodeFinder#removeNode(java.lang.String)
	 */
	@Override
	public boolean removeNode(String nodeName) {
		return super.removeNode(nodeName);
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.cluster.AbstractNodeFinder#removeAllNodes()
	 */
	@Override
	protected boolean removeAllNodes() {
		return super.removeAllNodes();
	}
	
	public boolean readNodes(InputStream in) throws IOException {
		if (in == null) {
			return false;
		}
		
		return readNodes(new InputStreamReader(in));
	}
	
	public boolean readNodes(Reader reader) throws IOException {
		if (reader == null) {
			return false;
		}
		
		boolean foundNodes = false;
		try {
			BufferedReader buff = new BufferedReader(reader);
			String line = null;
			while ((line = buff.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					// ignore comments
					continue;
				}
				
				int pos = line.indexOf("#");
				if (pos >= 0) {
					// comment starts somewhere in the middle of the line
					line = line.substring(0, pos);
				}
				
				String nodeName = line.trim();
				if (nodeName.length() == 0) {
					// empty node name
					continue;
				}
				if (addNode(nodeName)) {
					foundNodes = true;
				}
			}
		}
		finally {
			try {
				reader.close();
			}
			catch (Throwable t) {
				// ignore
			}
		}
		
		return foundNodes;
	}
}

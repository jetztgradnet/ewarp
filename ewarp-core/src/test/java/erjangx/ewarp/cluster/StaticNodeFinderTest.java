package erjangx.ewarp.cluster;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class StaticNodeFinderTest {
	public final static String NODE1 = "node1";
	public final static String NODE2 = "node2";
	public final static String NODE3 = "node3";
	public final static String NODE4 = "node4";
	public final static String NODE5 = "node5";
	
	protected StaticNodeFinder nodeFinder;
	
	@Before
	public void setup() {
		nodeFinder = new StaticNodeFinder();
	}

	@Test
	public void testAddRemoveNode() throws Exception {
		final RecordingNodeListener listener = new RecordingNodeListener();
		final List<String> events = listener.getEvents();
		
		nodeFinder.addNodeListener(listener);
		nodeFinder.addNode(NODE1);
		assertThat(events.size(), is(1));
		assertThat(events.get(0), is("added:" + NODE1));
		
		listener.clear();
		
		// add node again
		nodeFinder.addNode(NODE1);
		
		// no more events, as node is already known
		assertThat(events.size(), is(0));
		
		// add more nodes
		nodeFinder.addNode(NODE2);
		nodeFinder.addNode(NODE3);
		
		assertThat(nodeFinder.getKnownNodes().size(), is(3));
		
		listener.clear();
		
		// remove node
		nodeFinder.removeNode(NODE1);
		assertThat(events.size(), is(1));
		assertThat(events.get(0), is("removed:" + NODE1));
	}
	
	@Test
	public void testSyncNodesWithNoChanges() throws Exception {
		final RecordingNodeListener listener = new RecordingNodeListener();
		final List<String> events = listener.getEvents();
		
		nodeFinder.addNodeListener(listener);
		nodeFinder.addNode(NODE1);
		nodeFinder.addNode(NODE2);
		nodeFinder.addNode(NODE3);
		
		Set<String> nodesToSync = new HashSet<String>();
		nodesToSync.add(NODE1);
		nodesToSync.add(NODE2);
		nodesToSync.add(NODE3);
		
		listener.clear();
		
		boolean changes = nodeFinder.syncNodes(nodesToSync);
		
		assertThat(changes, is(false));
		assertThat(events.size(), is(0));
	}
	
	@Test
	public void testSyncNodes() throws Exception {
		final RecordingNodeListener listener = new RecordingNodeListener();
		final List<String> events = listener.getEvents();
		
		nodeFinder.addNodeListener(listener);
		nodeFinder.addNode(NODE1);
		nodeFinder.addNode(NODE2);
		nodeFinder.addNode(NODE3);
		
		Set<String> nodesToSync = new HashSet<String>();
		nodesToSync.add(NODE3);
		nodesToSync.add(NODE4);
		nodesToSync.add(NODE5);
		
		listener.clear();
		
		boolean changes = nodeFinder.syncNodes(nodesToSync);
		
		assertThat(changes, is(true));
		assertThat(events.size(), is(4));
		assertThat(events.contains("removed:" + NODE1), is(true));
		assertThat(events.contains("removed:" + NODE2), is(true));
		assertThat(events.contains("removed:" + NODE3), is(false));
		assertThat(events.contains("added:" + NODE3), is(false));
		assertThat(events.contains("added:" + NODE4), is(true));
		assertThat(events.contains("added:" + NODE5), is(true));
	}
	
	@Test
	public void testRemoveAllNodes() throws Exception {
		final RecordingNodeListener listener = new RecordingNodeListener();
		final List<String> events = listener.getEvents();
		
		nodeFinder.addNodeListener(listener);
		nodeFinder.addNode(NODE1);
		nodeFinder.addNode(NODE2);
		nodeFinder.addNode(NODE3);
		
		assertThat(nodeFinder.size(), is(3));
		assertThat(events.size(), is(3));
		
		listener.clear();
		
		boolean changes = nodeFinder.removeAllNodes();
		
		assertThat(changes, is(true));
		assertThat(nodeFinder.size(), is(0));
		assertThat(events.size(), is(3));
	}
	
	@Test
	public void testReadNodes() throws Exception {
		final RecordingNodeListener listener = new RecordingNodeListener();
		final List<String> events = listener.getEvents();
		
		StringWriter text = new StringWriter();
		PrintWriter buff = new PrintWriter(text);
		buff.println("# some comment");
		buff.println(" " + NODE1 + " ");
		buff.println(NODE2 + "   # another comment");
		buff.println(" ");
		buff.println(NODE3);
		
		String config = text.toString();
		
		nodeFinder.addNodeListener(listener);
		nodeFinder.readNodes(new StringReader(config));
		
		assertThat(nodeFinder.size(), is(3));
		assertThat(events.size(), is(3));
		assertThat(nodeFinder.hasNode(NODE1), is(true));
		assertThat(nodeFinder.hasNode(NODE2), is(true));
		assertThat(nodeFinder.hasNode(NODE3), is(true));
		assertThat(nodeFinder.hasNode(NODE4), is(false));
		assertThat(nodeFinder.hasNode(NODE5), is(false));
	}

}

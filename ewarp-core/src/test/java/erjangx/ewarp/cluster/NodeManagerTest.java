package erjangx.ewarp.cluster;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class NodeManagerTest {
	public final static String NODE1 = "node1";
	public final static String NODE2 = "node2";
	public final static String NODE3 = "node3";
	public final static String NODE4 = "node4";
	public final static String NODE5 = "node5";
	
	protected NodeManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new NodeManager();
	}
	
	@Test
	public void testMultipleNodeFinders() throws Exception {
		final RecordingNodeListener listener = new RecordingNodeListener();
		final List<String> events = listener.getEvents();
		
		manager.addNodeListener(listener);
		
		final StaticNodeFinder finder1 = new StaticNodeFinder();
		finder1.addNode(NODE1);
		finder1.addNode(NODE2);
		finder1.addNode(NODE3);
		
		final StaticNodeFinder finder2 = new StaticNodeFinder();
		finder2.addNode(NODE3);
		finder2.addNode(NODE4);
		finder2.addNode(NODE5);
		
		manager.addNodeFinder(finder1);
		assertThat(events.size(), is(3));
		
		listener.clear();
		
		manager.addNodeFinder(finder2);
		// NODE3 is already known, so there should only be 2 events
		assertThat(events.size(), is(2));
		
		listener.clear();
		manager.removeNodeFinder(finder1);
		assertThat(events.size(), is(2));
		assertThat(manager.size(), is(3));
	}

}

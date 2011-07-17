package erjangx.ewarp.runtime.stats;

import erjang.EAtom;
import erjang.ECons;
import erjang.EObject;
import erjang.EPeer;

/**
 * Collect node info.
 * 
 * @author wolfgang
 */
public class NodeCollector extends AbstractListStatusCollector<String, String> {
	public final static StatusName NODES = new StatusName("nodes", "Nodes");

	public NodeCollector() {
		super("nodes", "Nodes", NODES, false);
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getIdentifier(erjang.EObject)
	 */
	@Override
	protected String getIdentifier(EObject element) {
		// we expect the handle to be an atom
		EAtom node = element.testAtom();
		if (node != null) {
			return node.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getInfo(erjang.EObject)
	 */
	@Override
	protected String getInfo(EObject element) {
		// we return a list, so there's no info object
		return null;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getList()
	 */
	@Override
	protected ECons getDataToCollect() {
		return EPeer.getRemoteNodes();
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getListStatusName()
	 */
	@Override
	protected StatusName getListStatusName() {
		return NODES;
	}
}

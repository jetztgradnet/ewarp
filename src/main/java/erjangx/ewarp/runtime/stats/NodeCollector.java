package erjangx.ewarp.runtime.stats;

import erjang.EAtom;
import erjang.ECons;
import erjang.EObject;

/**
 * Collect node info.
 * 
 * @author wolfgang
 */
public class NodeCollector extends AbstractListStatusCollector<String, String> {
	public final StatusName NODES = new StatusName("nodes", "Nodes");

	public NodeCollector() {
		super("nodes", "Nodes");
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getIdentifier(erjang.EObject)
	 */
	@Override
	protected String getIdentifier(EObject handle) {
		// we expect the handle to be an atom
		EAtom node = handle.testAtom();
		if (node != null) {
			return node.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getInfo(erjang.EObject)
	 */
	@Override
	protected String getInfo(EObject handle) {
		return handle.toString();
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getList()
	 */
	@Override
	protected ECons getList() {
		// TODO get nodes list
		return null;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getListStatusName()
	 */
	@Override
	protected StatusName getListStatusName() {
		return NODES;
	}
}

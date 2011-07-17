package erjangx.ewarp.util;

import java.util.Iterator;

import erjang.ECons;
import erjang.EObject;

public class EObjectIterator implements Iterator<EObject> {
	private ECons cons = null;

	public EObjectIterator(ECons cons) {
		this.cons = cons;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return ((cons != null) && !cons.isNil());
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public EObject next() {
		EObject next = cons.head();
		cons = cons.tail().testCons();
		return next;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		// not supported
		throw new UnsupportedOperationException();
	}
}

package erjangx.ewarp.runtime.stats;

import erjang.EAtom;
import erjang.ECons;
import erjang.EObject;
import erjang.ERT;
import erjang.ETuple;
import erjang.RPC;

public class ApplicationCollector extends AbstractListStatusCollector<String, String> {
	public final static StatusName APPS = new StatusName("modules", "Modules");
	public final static EAtom am_application = EAtom.intern("application");
	public final static EAtom am_which_applications = EAtom.intern("which_applications");

	public ApplicationCollector() {
		super("applications", APPS, true);
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getIdentifier(erjang.EObject)
	 */
	@Override
	protected String getIdentifier(EObject element) {
		ETuple tuple = element.testTuple();
		if ((tuple != null)
			&& (tuple.arity() > 0)) {
			return tuple.elm(1).toString();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getInfo(erjang.EObject)
	 */
	@Override
	protected String getInfo(EObject element) {
		ETuple tuple = element.testTuple();
		if ((tuple != null)
			&& (tuple.arity() > 2)) {
			return tuple.elm(3).toString();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getDataToCollect()
	 */
	@Override
	protected ECons getDataToCollect() {
		EObject result = RPC.call(am_application, am_which_applications, ERT.NIL);
		// result is {ok, funresult}
		ETuple resTuple = result.testTuple();
		if ((resTuple != null)
			&& (resTuple.arity() >= 2)) {
			// unwrap tuple
			result = resTuple.elm(2);
		}
		ECons apps = result.testCons();
		return apps;
	}
}

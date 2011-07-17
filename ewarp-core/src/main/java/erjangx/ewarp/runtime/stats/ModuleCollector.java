package erjangx.ewarp.runtime.stats;

import java.util.List;

import erjang.EAtom;
import erjang.ECons;
import erjang.EModuleManager;
import erjang.EObject;
import erjang.ESeq;
import erjang.m.erlang.ErlBif;
import erjangx.ewarp.util.EConvert;

/**
 * Collect module info. Currently a list of module names is return 
 * under the {@link #MODULES} status name.
 * 
 * TODO create some ModuleInfo object and return a map.
 * 
 * @author wolfgang
 */
public class ModuleCollector extends AbstractListStatusCollector<String, List<EObject>> {
	public final static StatusName MODULES = new StatusName("modules", "Modules");

	public ModuleCollector() {
		super("modules", "Modules", MODULES, true);
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getIdentifier(erjang.EObject)
	 */
	@Override
	protected String getIdentifier(EObject element) {
		EAtom name = element.testAtom();
		if (name != null) {
			return name.getName();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getInfo(erjang.EObject)
	 */
	@Override
	protected List<EObject> getInfo(EObject element) {
		EAtom module = element.testAtom();
		if (module == null) {
			return null;
		}
		// TODO maybe create a map with relevant data?
		return EConvert.toList(ErlBif.get_module_info(module));
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getList()
	 */
	@Override
	protected ECons getDataToCollect() {
		ESeq modules = EModuleManager.loaded_modules();
		return modules;
	}
}

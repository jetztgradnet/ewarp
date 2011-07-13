package erjangx.ewarp.runtime.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import erjang.EAtom;
import erjang.EModuleManager;
import erjang.ESeq;
import erjangx.ewarp.runtime.ErjangRuntime;
import erjangx.ewarp.util.EObjectIterator;

public class ModuleCollector extends AbstractStatusCollector {
	public final StatusName MODULES = new StatusName("modules", "Modules");

	public ModuleCollector() {
		super("modules", "Modules");
	}

	public boolean collectStatus(ErjangRuntime runtime,
			Map<StatusName, Object> status) {
		List<String> moduleNames = new ArrayList<String>();
		ESeq modules = EModuleManager.loaded_modules();
		for (EObjectIterator it = new EObjectIterator(modules); it.hasNext();) {
			EAtom name = it.next().testAtom();
			if (name == null) {
				continue;
			}
			moduleNames.add(name.toString());
		}
		
		collect(status, MODULES, moduleNames);
		
		return true;
	}
}

package erjangx.ewarp.runtime.stats;

import java.util.Map;

import erjang.EModuleManager;
import erjang.EProc;
import erjangx.ewarp.runtime.ErjangRuntime;

public class InfoCollector extends AbstractStatusCollector {
	public final StatusName PROCESS_COUNT = new StatusName("process_count", "Process count");
	public final StatusName MODULE_COUNT = new StatusName("module_count", "Module count");

	public InfoCollector() {
		super("info", "General information");
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.StatusCollector#collectStatus(erjangx.ewarp.runtime.ErjangRuntime, java.util.Map)
	 */
	public boolean collectStatus(ErjangRuntime runtime,
			Map<StatusName, Object> status) {
		collect(status, PROCESS_COUNT, EProc.process_count());
		collect(status, MODULE_COUNT, EModuleManager.loaded_modules().length());
		return true;
	}

}

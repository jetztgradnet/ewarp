package erjangx.ewarp.runtime.stats;

import java.util.HashMap;
import java.util.Map;

import erjang.EInternalPID;
import erjang.EProc;
import erjang.ESeq;
import erjangx.ewarp.runtime.ErjangRuntime;
import erjangx.ewarp.util.EObjectIterator;

public class ProcessListCollector extends AbstractStatusCollector {
	public final StatusName PROCESSES = new StatusName("processes", "Processes");

	public ProcessListCollector() {
		super("Processes", "processes");
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.StatusCollector#collectStatus(erjangx.ewarp.runtime.ErjangRuntime, java.util.Map)
	 */
	public boolean collectStatus(ErjangRuntime runtime,
			Map<StatusName, Object> status) {
		Map<EInternalPID, Object> processInfo = new HashMap<EInternalPID, Object>();
		
		ESeq processes = EProc.processes();
		
		for (EObjectIterator it = new EObjectIterator(processes); it.hasNext();) {
			EInternalPID pid = it.next().testInternalPID();
			if (pid == null) {
				continue;
			}
			EProc proc = null; // TODO lookup process
			Object info = getStatus(pid, proc);
			if (info != null) {
				processInfo.put(pid, info);
			}
		}
		
		collect(status, PROCESSES, processInfo);
		
		return true;
	}
	
	protected Object getStatus(EInternalPID pid, EProc process) {
		if (process != null) {
			return process.process_info();
		}
		return pid;
	}
}

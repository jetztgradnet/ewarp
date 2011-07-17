package erjangx.ewarp.runtime.stats;

import erjang.EAtom;
import erjang.ECons;
import erjang.EInternalPID;
import erjang.EObject;
import erjang.EProc;
import erjang.ESeq;
import erjangx.ewarp.util.EConvert;

/**
 * Collect process info.
 * 
 * @author wolfgang
 */
public class ProcessCollector extends AbstractListStatusCollector<String, ProcessInfo> {
	public final static StatusName PROCESSES = new StatusName("processes", "Processes");
	
	// TODO remove, when EProc.am_status is public
	static final EObject am_status = EAtom.intern("status");

	public ProcessCollector() {
		super("Processes", "processes", PROCESSES, true);
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getIdentifier(erjang.EObject)
	 */
	@Override
	protected String getIdentifier(EObject element) {
		EInternalPID pid = element.testInternalPID();
		if (pid != null) {
			return pid.toString();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getInfo(erjang.EObject)
	 */
	@Override
	protected ProcessInfo getInfo(EObject element) {
		EInternalPID pid = element.testInternalPID();
		if (pid == null) {
			return null;
		}
		
		String registeredName = EConvert.toString(pid.process_info(EProc.am_registered_name));
		String initialCall = EConvert.toString(pid.process_info(EProc.am_initial_call));
		String currentCall = EConvert.toString(pid.process_info(EProc.am_current_function));
		String status = EConvert.toString(pid.process_info(/*EProc.*/am_status));
		int messageQueueLength = EConvert.toInteger(pid.process_info(EProc.am_message_queue_len));
		return new ProcessInfo(pid.toString(), registeredName, initialCall, currentCall, status, messageQueueLength);
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.AbstractListStatusCollector#getList()
	 */
	@Override
	protected ECons getDataToCollect() {
		ESeq processes = EProc.processes();
		return processes;
	}
}

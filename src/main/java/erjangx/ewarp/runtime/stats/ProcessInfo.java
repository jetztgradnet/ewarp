package erjangx.ewarp.runtime.stats;

public class ProcessInfo {
	private final String pid;
	private final String registeredName;
	private final String initialCall;
	private final String currentCall;
	private final String status;
	private final int messageQueueLength;
	
	public ProcessInfo(String pid) {
		this(pid, null, null, null, null, 0);
	}

	public ProcessInfo(String pid, String registeredName, String initialCall, String currentCall, String status, int messageQueueLength) {
		this.pid = pid;
		this.registeredName = registeredName;
		this.initialCall = initialCall;
		this.currentCall = currentCall;
		this.status = status;
		this.messageQueueLength = messageQueueLength;
	}
	
	public String getPid() {
		return pid;
	}
	
	public String getRegisteredName() {
		return registeredName;
	}
	
	public String getInitialCall() {
		return initialCall;
	}
	
	public String getCurrentCall() {
		return currentCall;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getMessageQueueLength() {
		return messageQueueLength;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return pid.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessInfo) {
			ProcessInfo other = (ProcessInfo) obj;
			return pid.equals(other.getPid());
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		
		buff.append("pid=");
		buff.append(pid);
		buff.append(", registeredName=");
		buff.append(registeredName);
		buff.append(", initialCall=");
		buff.append(initialCall);
		buff.append(", currentCall=");
		buff.append(currentCall);
		buff.append(", status=");
		buff.append(status);
		buff.append(", messageQueueLength=");
		buff.append(messageQueueLength);
		return buff.toString();
	}
}
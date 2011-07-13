package erjangx.ewarp.runtime.stats;

import java.util.Map;

public abstract class AbstractStatusCollector implements StatusCollector {
	
	private final String name;
	private final String type;

	public AbstractStatusCollector(String type) {
		this(type, type);
	}
	
	public AbstractStatusCollector(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.StatusCollector#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see erjangx.ewarp.runtime.stats.StatusCollector#getType()
	 */
	public String getType() {
		return type;
	}

	protected void collect(Map<StatusName, Object> status, String name, Object value) {
		collect(status, new StatusName(name), value);
	}
	
	protected void collect(Map<StatusName, Object> status, StatusName name, Object value) {
		status.put(name, value);
	}
	
	protected void collect(Map<StatusName, Object> status, String name, int value) {
		collect(status, new StatusName(name), value);
	}
	
	protected void collect(Map<StatusName, Object> status, StatusName name, int value) {
		status.put(name, new Integer(value));
	}
	
	protected void collect(Map<StatusName, Object> status, String name, long value) {
		collect(status, new StatusName(name), value);
	}
	
	protected void collect(Map<StatusName, Object> status, StatusName name, long value) {
		status.put(name, new Long(value));
	}
	
	protected void collect(Map<StatusName, Object> status, String name, double value) {
		collect(status, new StatusName(name), value);
	}
	
	protected void collect(Map<StatusName, Object> status, StatusName name, double value) {
		status.put(name, new Double(value));
	}
	
	protected void collect(Map<StatusName, Object> status, String name, boolean value) {
		collect(status, new StatusName(name), value);
	}
	
	protected void collect(Map<StatusName, Object> status, StatusName name, boolean value) {
		status.put(name, Boolean.valueOf(value));
	}
	
	protected void collect(Map<StatusName, Object> status, String name, char value) {
		collect(status, new StatusName(name), value);
	}
	
	protected void collect(Map<StatusName, Object> status, StatusName name, char value) {
		status.put(name, "" + value);
	}

}

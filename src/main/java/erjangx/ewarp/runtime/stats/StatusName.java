package erjangx.ewarp.runtime.stats;

public class StatusName implements Comparable<Object> {
	
	private final String shortName;
	private final String displayName;

	public StatusName(String shortName) {
		this(shortName, shortName);
	}
	
	public StatusName(String shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return shortName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatusName) {
			StatusName other = (StatusName) obj;
			return shortName.equals(other.getShortName());
		}
		else if (obj instanceof String) {
			String other = (String) obj;
			return shortName.equals(other);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return shortName.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		if (obj instanceof StatusName) {
			StatusName other = (StatusName) obj;
			return shortName.compareTo(other.getShortName());
		}
		else if (obj instanceof String) {
			String other = (String) obj;
			return shortName.compareTo(other);
		}
		
		return 0;
	}
	
}

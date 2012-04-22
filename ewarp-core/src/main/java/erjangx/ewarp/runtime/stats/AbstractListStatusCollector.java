package erjangx.ewarp.runtime.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import erjang.ECons;
import erjang.EObject;
import erjang.EObjectIterator;
import erjang.ESeq;
import erjangx.ewarp.runtime.ErjangRuntime;

/**
 * Collector for data, which is returned as list ({@link ECons} or {@link ESeq}). 
 * The list data is stored under the {@link StatusName} provided in the constructor.
 * 
 * For each value in the collected data list, an identifier is obtained using 
 * {@link #getIdentifier(EObject)}, and, if {@link #useMap} returns <code>true</code>, 
 * an info object containing additional details is obtained using {@link #getInfo(EObject)}.
 * 
 * @param <NT> identifier type
 * @param <VT> info object type. Is only used if {@link #useMap} returns <code>true</code>
 * 
 * @author wolfgang
 */
public abstract class AbstractListStatusCollector<NT, VT> extends AbstractStatusCollector {
	private final StatusName statusName;
	private final boolean useMap;
	
	public AbstractListStatusCollector(String type, StatusName statusName, boolean useMap) {
		super(type);
		this.statusName = statusName;
		this.useMap = useMap;
	}

	public AbstractListStatusCollector(String name, String type, StatusName statusName, boolean useMap) {
		super(name, type);
		this.statusName = statusName;
		this.useMap = useMap;
	}

	public boolean collectStatus(ErjangRuntime runtime,
			Map<StatusName, Object> status) {
		if (isMap()) {
			return collectStatusMap(runtime, status);
		}
		else {
			return collectStatusList(runtime, status);
		}
	}
	
	protected boolean collectStatusMap(ErjangRuntime runtime,
			Map<StatusName, Object> status) {
		
		Map<NT, VT> results = new HashMap<NT, VT>();
		
		ECons list = getDataToCollect();
		for (EObjectIterator it = new EObjectIterator(list); it.hasNext();) {
			EObject handle = it.next();
			if (handle == null) {
				continue;
			}
			NT id = getIdentifier(handle);
			if (id == null) {
				// skip element
				continue;
			}
			VT info = getInfo(handle);
			if (info != null) {
				results.put(id, info);
			}
		}
		
		// store map under status name
		StatusName statusName = getListStatusName();
		collect(status, statusName, results);
		
		return true;
	}
	
	protected boolean collectStatusList(ErjangRuntime runtime,
			Map<StatusName, Object> status) {
		
		List<NT> results = new ArrayList<NT>();
		
		ECons list = getDataToCollect();
		for (EObjectIterator it = new EObjectIterator(list); it.hasNext();) {
			EObject element = it.next();
			if (element == null) {
				continue;
			}
			NT id = getIdentifier(element);
			if (id == null) {
				// skip element
				continue;
			}
			results.add(id);
		}
		
		// store list under status name
		StatusName statusName = getListStatusName();
		collect(status, statusName, results);
		
		return true;
	}
	
	protected abstract NT getIdentifier(EObject element);
	
	/**
	 * Get list containing data to collect.
	 * 
	 * @return list containing data to collect
	 */
	protected abstract ECons getDataToCollect();
	
	protected StatusName getListStatusName() {
		return statusName;
	}
	
	/**
	 * Create info object for an element of the list
	 * returned by {@link #getDataToCollect()}.
	 *  
	 * @param element current data element
	 * 
	 * @return info object or <code>null</code> to skip the
	 * 			value from being part of the collected data
	 */
	protected VT getInfo(EObject element) {
		return null;
	}

	/**
	 * Determine whether to collect a {@link List} or {@link Map}.
	 * 
	 * @return <code>true</code> to collect a {@link Map} with
	 * 			an info object returned by {@link #getInfo(EObject)},
	 * 			<code>false</code> to cllect a {@link List} with only
	 * 			an identifier for each data element.
	 */
	public boolean isMap() { 
		return useMap; 
	}

}

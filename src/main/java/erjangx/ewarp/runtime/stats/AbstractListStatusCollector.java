package erjangx.ewarp.runtime.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import erjang.ECons;
import erjang.EObject;
import erjangx.ewarp.runtime.ErjangRuntime;
import erjangx.ewarp.util.EObjectIterator;

public abstract class AbstractListStatusCollector<NT, VT> extends AbstractStatusCollector {
	public AbstractListStatusCollector(String type) {
		super(type);
	}

	public AbstractListStatusCollector(String name, String type) {
		super(name, type);
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
		
		ECons list = getList();
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
		
		ECons list = getList();
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
			results.add(id);
		}
		
		// store list under status name
		StatusName statusName = getListStatusName();
		collect(status, statusName, results);
		
		return true;
	}
	
	protected abstract NT getIdentifier(EObject handle);
	protected abstract VT getInfo(EObject handle);
	protected abstract ECons getList();
	protected abstract StatusName getListStatusName();
	protected boolean isMap() { return true; }

}

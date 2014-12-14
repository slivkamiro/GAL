package algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmManager {

	private List<Class<?>> algorithms;

	public AlgorithmManager() {
		algorithms = new ArrayList<Class<?>>();
		algorithms.add(ChuLiuEdmonds.class);
		algorithms.add(ChuLiuEdmondsMax.class);
		// New algorithms have to be added here
	}

	public List<String> getAlgorithms() {
		List<String> names = new ArrayList<String>();
		for(Class<?> c : algorithms) {
			names.add(c.getSimpleName());
		}
		return names;
	}

	public Algorithm getAlgorithm(String name)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		for(Class<?> a : algorithms) {
			if(a.getSimpleName().equals(name)) {
				Constructor<?> constr = a.getConstructor();
				Object o = constr.newInstance();
				if(o instanceof Algorithm)
					return (Algorithm) o;
			}
		}
		return null;
	}
}

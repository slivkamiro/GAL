package com.slivkam.graphdemonstrator.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Miroslav
 *
 */
public class AlgorithmManager {

	private List<Class<?>> algorithms;

	/**
	 * Default constructor. Holds all known algorithm classes.
	 */
	public AlgorithmManager() {
		algorithms = new ArrayList<Class<?>>();
		algorithms.add(ChuLiuEdmonds.class);
		algorithms.add(ChuLiuEdmondsMax.class);
		// New algorithms have to be added here
	}

	/**
	 * Gets names of algorithms based on class name.
	 * @return List of known algorithms names.
	 */
	public List<String> getAlgorithms() {
		List<String> names = new ArrayList<String>();
		for(Class<?> c : algorithms) {
			names.add(c.getSimpleName());
		}
		return names;
	}

	/**
	 * Initialize algorithm class based on name given. Using Reflection API.
	 * @param name Name of the algorithm based on class name.
	 * @return new algorithm object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
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

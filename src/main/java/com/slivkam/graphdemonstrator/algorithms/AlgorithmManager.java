package com.slivkam.graphdemonstrator.algorithms;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Miroslav
 *
 */
public class AlgorithmManager {

    private List<Class<Algorithm>> algorithms;

    /**
     * Default constructor. Holds all known algorithm classes.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public AlgorithmManager() throws IOException, ClassNotFoundException {
        this.algorithms = new ArrayList<Class<Algorithm>>();

        String pkg = this.getClass().getPackage().getName().replace('.', '/');
        URL resource = this.getClass().getClassLoader().getResource(pkg);
        File algDir = new File(resource.getFile());
        for(File file : algDir.listFiles()) {
            if (file.isDirectory()) continue;
            Class<?> cls = Class.forName(pkg.replace("/", ".") + "." +file.getName().substring(0,file.getName().length()-6));
            if (Algorithm.class.isAssignableFrom(cls) && !cls.isAssignableFrom(Algorithm.class)) {
                this.algorithms.add((Class<Algorithm>) cls);
            }
        }
    }

    /**
     * Gets names of algorithms based on class name.
     * @return List of known algorithms names.
     */
    public List<String> getAlgorithms() {
        List<String> names = new ArrayList<String>();
        for(Class<Algorithm> c : this.algorithms) {
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
        for(Class<?> a : this.algorithms) {
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

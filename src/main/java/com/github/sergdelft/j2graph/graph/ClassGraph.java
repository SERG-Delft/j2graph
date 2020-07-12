package com.github.sergdelft.j2graph.graph;

import java.util.Collections;
import java.util.List;

public class ClassGraph {

    private final List<MethodGraph> methods;
    private final String className;

    public ClassGraph(String className, List<MethodGraph> methods) {
        this.className = className;
        this.methods = methods;
    }

    public List<MethodGraph> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    public String getClassName() {
        return className;
    }
}

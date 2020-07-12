package com.github.sergdelft.j2graph.graph;

public class NonTerminalMethodInvocation extends NonTerminal {
    private final String invokedMethod;

    public NonTerminalMethodInvocation(NonTerminalType type, String invokedMethod) {
        super(type);
        this.invokedMethod = invokedMethod;
    }

    public String getInvokedMethod() {
        return invokedMethod;
    }

    @Override
    public String getName() {
        return String.format("%s:%s", super.getName(), invokedMethod);
    }
}

package com.github.sergdelft.j2graph.graph;

public class Symbol {
    private static int COUNTER = 0;

    private final int id;
    private final String symbol;

    public Symbol(String symbol) {
        this.id = ++COUNTER;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

}

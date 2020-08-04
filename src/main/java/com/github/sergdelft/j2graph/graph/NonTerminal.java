package com.github.sergdelft.j2graph.graph;

import java.util.ArrayList;
import java.util.List;

public class NonTerminal {
    private static int COUNTER = 0;

    private int id;
    private String type;
    private List<NonTerminal> children;
    private List<Token> tokens;

    public NonTerminal(String type) {
        this.id = ++COUNTER;

        this.type = type;
        this.children = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public void addChild(NonTerminal node) {
        children.add(node);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<NonTerminal> getChildren() {
        return children;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getName() {
        return type;
    }

    @Override
    public String toString() {
        return "NonTerminal{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}

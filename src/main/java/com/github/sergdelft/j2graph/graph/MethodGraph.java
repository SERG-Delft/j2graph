package com.github.sergdelft.j2graph.graph;

import java.util.Collection;
import java.util.LinkedList;

public class MethodGraph {
    private final String methodName;
    private final NonTerminal root;
    private final Collection<NonTerminal> nonTerminals;
    private final LinkedList<Token> tokens;
    private final Collection<Symbol> symbols;
    private final Collection<Vocabulary> vocabulary;

    public MethodGraph(String methodName, NonTerminal root, Collection<NonTerminal> nonTerminals, LinkedList<Token> tokens, Collection<Symbol> symbols, Collection<Vocabulary> vocabulary) {
        this.methodName = methodName;
        this.root = root;
        this.nonTerminals = nonTerminals;
        this.tokens = tokens;
        this.symbols = symbols;
        this.vocabulary = vocabulary;
    }

    public String getMethodName() {
        return methodName;
    }

    public NonTerminal getRoot() {
        return root;
    }

    public Collection<NonTerminal> getNonTerminals() {
        return nonTerminals;
    }

    public LinkedList<Token> getTokens() {
        return tokens;
    }

    public Collection<Symbol> getSymbols() {
        return symbols;
    }

    public Collection<Vocabulary> getVocabulary() {
        return vocabulary;
    }

    @Override
    public String toString() {
        return "MethodGraph{" +
                "methodName='" + methodName + '\'' +
                '}';
    }
}

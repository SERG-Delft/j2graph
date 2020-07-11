package com.github.sergdelft.j2graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodGraph {
    private final String methodName;
    private final NonTerminal root;
    private final List<NonTerminal> nonTerminals;
    private final LinkedList<Token> tokens;
    private final Collection<Symbol> symbols;
    private final Collection<Vocabulary> vocabulary;

    public MethodGraph(String methodName, NonTerminal root, List<NonTerminal> nonTerminals, LinkedList<Token> tokens, Collection<Symbol> symbols, Collection<Vocabulary> vocabulary) {
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

    public List<NonTerminal> getNonTerminals() {
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
}

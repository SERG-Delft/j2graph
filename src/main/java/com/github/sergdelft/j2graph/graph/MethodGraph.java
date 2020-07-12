package com.github.sergdelft.j2graph.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    // finds all NonTerminal nodes that are actual method invocations
    public List<NonTerminalMethodInvocation> methodInvocations() {
        return
            nonTerminals.stream().filter(nt -> nt instanceof NonTerminalMethodInvocation)
                .map(nt -> (NonTerminalMethodInvocation) nt)
                .collect(Collectors.toList());
    }

    // find all 'return' tokens and add returns to edge to the specified non terminal
    public void returnsTo(NonTerminalMethodInvocation nonTerminal) {
        tokens.stream().filter(t -> t.isReturn())
                .forEach(t -> t.returnsTo(nonTerminal));
    }
}

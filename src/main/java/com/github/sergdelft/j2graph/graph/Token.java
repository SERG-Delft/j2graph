package com.github.sergdelft.j2graph.graph;

import java.util.Optional;
import java.util.Set;

public class Token {
    private static int COUNTER = 0;

    private final String tokenName;
    private final int id;

    private Symbol symbol;
    private NonTerminal assignedFrom;
    private Token nextLexicalUse;
    private Set<Vocabulary> vocabulary;

    public Token(String tokenName) {
        this.id = ++COUNTER;
        this.tokenName = tokenName;
    }

    public void forSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void withVocabulary(Set<Vocabulary> tokenVocabulary) {
        this.vocabulary = tokenVocabulary;
    }

    public void nextLexicalUse(Token nextLexicalUse) {
        this.nextLexicalUse = nextLexicalUse;
    }

    public void assignedFrom(NonTerminal assignedFrom) {
        this.assignedFrom = assignedFrom;
    }

    public String getTokenName() {
        return tokenName;
    }

    public int getId() {
        return id;
    }

    public Optional<Symbol> getSymbol() {
        return Optional.ofNullable(symbol);
    }

    public Optional<Set<Vocabulary>> getVocabulary() {
        return Optional.ofNullable(vocabulary);
    }

    public Optional<Token> getNextLexicalUse() {
        return Optional.ofNullable(nextLexicalUse);
    }

    public Optional<NonTerminal> getAssignedFrom() {
        return Optional.ofNullable(assignedFrom);
    }

    public boolean sameAs(String tokenName) {
        return this.tokenName.equals(tokenName);
    }
}

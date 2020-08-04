package com.github.sergdelft.j2graph.graph;

import java.util.*;

public class Token {
    private static int COUNTER = 0;

    private final String tokenName;
    private int id;

    private Symbol symbol;
    private NonTerminal assignedFrom;
    private Token nextLexicalUse;
    private Set<Vocabulary> vocabulary;
    private List<NonTerminal> returnsTo;

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

    public void setId(int id) {
        this.id = id;
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

    public boolean isReturn() {
        return tokenName.equals("return");
    }

    public void returnsTo(NonTerminalMethodInvocation nonTerminal) {
        if(returnsTo == null)
            returnsTo = new ArrayList<>();

        returnsTo.add(nonTerminal);
    }

    public List<NonTerminal> getListOfNonTerminalsToReturnTo() {
        return returnsTo == null ? Collections.emptyList() : returnsTo;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenName='" + tokenName + '\'' +
                ", id=" + id +
                '}';
    }
}

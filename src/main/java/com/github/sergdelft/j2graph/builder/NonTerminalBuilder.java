package com.github.sergdelft.j2graph.builder;

import com.github.sergdelft.j2graph.graph.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class NonTerminalBuilder {

    private final MethodGraphBuilder context;
    private final NonTerminal node;

    public NonTerminalBuilder(MethodGraphBuilder context, NonTerminal node) {
        this.context = context;
        this.node = node;
    }

    public Pair<Symbol, Token> symbol(String symbolName) {
        Token token = token(symbolName, true, true);

        Symbol symbol = context.symbol(symbolName);
        token.forSymbol(symbol);

        return Pair.of(symbol, token);
    }

    public Token token(String word) {
        return token(word,false, false);
    }

    public Token token(String word, boolean addVocabulary, boolean nextLexicalUse) {
        Token token = context.token(word,nextLexicalUse);
        node.addToken(token);

        if(addVocabulary) {
            Set<Vocabulary> tokenVocabulary = context.addVocabulary(word);
            token.withVocabulary(tokenVocabulary);
        }

        return token;
    }

    public NonTerminal getNode() {
        return node;
    }

    private NonTerminalBuilder nonTerminal(NonTerminal newNode) {
        this.node.addChild(newNode);
        this.context.addNonTerminal(newNode);

        return new NonTerminalBuilder(context, newNode);
    }

    public NonTerminalBuilder nonTerminal(String type) {
        NonTerminal newNode = new NonTerminal(type);
        return nonTerminal(newNode);
    }

    public NonTerminalBuilder methodInvocation(String type, String invokedMethod) {
        NonTerminal newNode = new NonTerminalMethodInvocation(type, invokedMethod);
        return nonTerminal(newNode);
    }
}

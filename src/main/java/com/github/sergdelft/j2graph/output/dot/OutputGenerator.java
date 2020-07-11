package com.github.sergdelft.j2graph.output.dot;

import com.github.sergdelft.j2graph.graph.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OutputGenerator {

    private MethodGraph method;
    private MethodGraphVisitor visitor;

    public void accept(MethodGraph method, MethodGraphVisitor visitor) {
        this.method = method;
        this.visitor = visitor;

        // info
        visitor.methodName(method.getMethodName());

        // nodes
        nonTerminals();
        tokens();
        symbols();
        vocabulary();

        // edges
        tokenEdges();
        nonTerminalTokenEdges();
        nonTerminalEdges(method.getRoot());
        tokenSymbols();
        tokenVocabulary();
        nextLexicalUse();
        assignedFromEdges();

        // end of the visit
        visitor.end();

    }

    private void assignedFromEdges() {
        for (Token token : method.getTokens()) {
            Optional<NonTerminal> possibleAssignedFrom = token.getAssignedFrom();
            if(possibleAssignedFrom.isPresent()) {
                NonTerminal assignedFrom = possibleAssignedFrom.get();
                visitor.assignedFrom(token, assignedFrom);
            }
        }
    }

    private void nextLexicalUse() {
        for (Token token : method.getTokens()) {
            Optional<Token> possibleNextLexicalUse = token.getNextLexicalUse();
            if(possibleNextLexicalUse.isPresent()) {
                Token nextLexicalUse = possibleNextLexicalUse.get();
                visitor.lexicalUse(token, nextLexicalUse);
            }
        }

    }

    private void tokenVocabulary() {
        for (Token token : method.getTokens()) {
            Optional<Set<Vocabulary>> possibleVocabulary = token.getVocabulary();
            if(possibleVocabulary.isPresent()) {
                Set<Vocabulary> vocabulary = possibleVocabulary.get();
                for (Vocabulary word : vocabulary) {
                    visitor.edge(word, token);
                }
            }
        }
    }

    private void tokenSymbols() {
        for (Token token : method.getTokens()) {
            Optional<Symbol> possibleSymbol = token.getSymbol();

            if(possibleSymbol.isPresent()) {
                Symbol symbol = possibleSymbol.get();
                visitor.edge(token, symbol);
            }
        }
    }

    private void nonTerminalTokenEdges() {
        for (NonTerminal node : method.getNonTerminals()) {
            List<Token> tokens = node.getTokens();
            for (Token token : tokens) {
                visitor.edge(node, token);
            }
        }
    }

    private void nonTerminalEdges(NonTerminal parent) {
        List<NonTerminal> children = parent.getChildren();
        for (NonTerminal child : children) {
            visitor.edge(parent, child);
            nonTerminalEdges(child);
        }
    }

    private void tokenEdges() {
        // there has to be at least two nodes to print the edge here
        LinkedList<Token> tokens = method.getTokens();

        if(tokens.size()<2)
            return;

        for (int i = 1; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            Token previousToken = tokens.get(i - 1);

            visitor.edge(previousToken, currentToken);
        }
    }

    private void vocabulary() {
        method.getVocabulary().stream().forEach(s -> visitor.vocabulary(s));
    }

    private void symbols() {
        method.getSymbols().stream().forEach(s -> visitor.symbol(s));
    }

    private void tokens() {
        method.getTokens().stream().forEach(t -> visitor.token(t));
    }

    private void nonTerminals() {
        method.getNonTerminals().stream().forEach(n -> visitor.nonTerminal(n));
    }
}

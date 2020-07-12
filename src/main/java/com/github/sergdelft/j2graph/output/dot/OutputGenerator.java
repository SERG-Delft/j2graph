package com.github.sergdelft.j2graph.output.dot;

import com.github.sergdelft.j2graph.graph.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OutputGenerator {

    public void accept(ClassGraph classGraph, MethodGraphVisitor visitor) {

        visitor.className(classGraph.getClassName());

        for (MethodGraph method : classGraph.getMethods()) {
            // info
            visitor.method(method.getMethodName(), method.getRoot());

            // nodes
            nonTerminals(method, visitor);
            tokens(method, visitor);
            symbols(method, visitor);
            vocabulary(method, visitor);

            // edges
            tokenEdges(method, visitor);
            nonTerminalTokenEdges(method, visitor);
            nonTerminalEdges(method.getRoot(), visitor);
            tokenSymbols(method, visitor);
            tokenVocabulary(method, visitor);
            nextLexicalUse(method, visitor);
            assignedFromEdges(method, visitor);

            visitor.endMethod(method.getMethodName(), method.getRoot());
        }

        // end of the visit
        visitor.end();

    }

    private void assignedFromEdges(MethodGraph method, MethodGraphVisitor visitor) {
        for (Token token : method.getTokens()) {
            Optional<NonTerminal> possibleAssignedFrom = token.getAssignedFrom();
            if(possibleAssignedFrom.isPresent()) {
                NonTerminal assignedFrom = possibleAssignedFrom.get();
                visitor.assignedFrom(token, assignedFrom);
            }
        }
    }

    private void nextLexicalUse(MethodGraph method, MethodGraphVisitor visitor) {
        for (Token token : method.getTokens()) {
            Optional<Token> possibleNextLexicalUse = token.getNextLexicalUse();
            if(possibleNextLexicalUse.isPresent()) {
                Token nextLexicalUse = possibleNextLexicalUse.get();
                visitor.lexicalUse(token, nextLexicalUse);
            }
        }

    }

    private void tokenVocabulary(MethodGraph method, MethodGraphVisitor visitor) {
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

    private void tokenSymbols(MethodGraph method, MethodGraphVisitor visitor) {
        for (Token token : method.getTokens()) {
            Optional<Symbol> possibleSymbol = token.getSymbol();

            if(possibleSymbol.isPresent()) {
                Symbol symbol = possibleSymbol.get();
                visitor.edge(token, symbol);
            }
        }
    }

    private void nonTerminalTokenEdges(MethodGraph method, MethodGraphVisitor visitor) {
        for (NonTerminal node : method.getNonTerminals()) {
            List<Token> tokens = node.getTokens();
            for (Token token : tokens) {
                visitor.edge(node, token);
            }
        }
    }

    private void nonTerminalEdges(NonTerminal parent, MethodGraphVisitor visitor) {
        List<NonTerminal> children = parent.getChildren();
        for (NonTerminal child : children) {
            visitor.edge(parent, child);
            nonTerminalEdges(child, visitor);
        }
    }

    private void tokenEdges(MethodGraph method, MethodGraphVisitor visitor) {
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

    private void vocabulary(MethodGraph method, MethodGraphVisitor visitor) {
        method.getVocabulary().stream().forEach(s -> visitor.vocabulary(s));
    }

    private void symbols(MethodGraph method, MethodGraphVisitor visitor) {
        method.getSymbols().stream().forEach(s -> visitor.symbol(s));
    }

    private void tokens(MethodGraph method, MethodGraphVisitor visitor) {
        method.getTokens().stream().forEach(t -> visitor.token(t));
    }

    private void nonTerminals(MethodGraph method, MethodGraphVisitor visitor) {
        method.getNonTerminals().stream().forEach(n -> visitor.nonTerminal(n));
    }
}

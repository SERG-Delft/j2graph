package com.github.sergdelft.j2graph.walker;

import com.github.sergdelft.j2graph.graph.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphWalker {

    public void accept(ClassGraph classGraph, Walker visitor) {

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
            returnsTo(method, visitor);

            visitor.endMethod(method.getMethodName(), method.getRoot());
        }

        // end of the visit
        visitor.end();

    }

    private void returnsTo(MethodGraph method, Walker visitor) {
        // find all return tokens in the current method
        List<Token> returnTokens = method.getTokens().stream()
                .filter(t -> t.isReturn())
                .collect(Collectors.toList());

        for (Token returnToken : returnTokens) {

            // if the return token has a list of non terminals to connect
            // we then invoke the visitor
            returnToken
                .getListOfNonTerminalsToReturnTo()
                .forEach(nt -> visitor.returnsTo(nt, returnToken));
        }
    }

    private void assignedFromEdges(MethodGraph method, Walker visitor) {
        for (Token token : method.getTokens()) {
            Optional<NonTerminal> possibleAssignedFrom = token.getAssignedFrom();
            if(possibleAssignedFrom.isPresent()) {
                NonTerminal assignedFrom = possibleAssignedFrom.get();
                visitor.assignedFrom(token, assignedFrom);
            }
        }
    }

    private void nextLexicalUse(MethodGraph method, Walker visitor) {
        for (Token token : method.getTokens()) {
            Optional<Token> possibleNextLexicalUse = token.getNextLexicalUse();
            if(possibleNextLexicalUse.isPresent()) {
                Token nextLexicalUse = possibleNextLexicalUse.get();
                visitor.nextLexicalUse(token, nextLexicalUse);
            }
        }

    }

    private void tokenVocabulary(MethodGraph method, Walker visitor) {
        for (Token token : method.getTokens()) {
            Optional<Set<Vocabulary>> possibleVocabulary = token.getVocabulary();
            if(possibleVocabulary.isPresent()) {
                Set<Vocabulary> vocabulary = possibleVocabulary.get();
                for (Vocabulary word : vocabulary) {
                    visitor.subtokenOf(word, token);
                }
            }
        }
    }

    private void tokenSymbols(MethodGraph method, Walker visitor) {
        for (Token token : method.getTokens()) {
            Optional<Symbol> possibleSymbol = token.getSymbol();

            if(possibleSymbol.isPresent()) {
                Symbol symbol = possibleSymbol.get();
                visitor.occurenceOf(token, symbol);
            }
        }
    }

    private void nonTerminalTokenEdges(MethodGraph method, Walker visitor) {
        for (NonTerminal node : method.getNonTerminals()) {
            List<Token> tokens = node.getTokens();
            for (Token token : tokens) {
                visitor.child(node, token);
            }
        }
    }

    private void nonTerminalEdges(NonTerminal parent, Walker visitor) {
        List<NonTerminal> children = parent.getChildren();
        for (NonTerminal child : children) {
            visitor.child(parent, child);
            nonTerminalEdges(child, visitor);
        }
    }

    private void tokenEdges(MethodGraph method, Walker visitor) {
        // there has to be at least two nodes to print the edge here
        LinkedList<Token> tokens = method.getTokens();

        if(tokens.size()<2)
            return;

        for (int i = 1; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            Token previousToken = tokens.get(i - 1);

            visitor.nextToken(previousToken, currentToken);
        }
    }

    private void vocabulary(MethodGraph method, Walker visitor) {
        method.getVocabulary().stream().forEach(s -> visitor.vocabulary(s));
    }

    private void symbols(MethodGraph method, Walker visitor) {
        method.getSymbols().stream().forEach(s -> visitor.symbol(s));
    }

    private void tokens(MethodGraph method, Walker visitor) {
        method.getTokens().stream().forEach(t -> visitor.token(t));
    }

    private void nonTerminals(MethodGraph method, Walker visitor) {
        method.getNonTerminals().stream().forEach(n -> visitor.nonTerminal(n));
    }
}

package com.github.sergdelft.j2graph.output.dot;

import com.github.sergdelft.j2graph.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DotGenerator {

    private final StringBuilder builder = new StringBuilder();

    public String generate(MethodGraph method) {

        builder.append(String.format("digraph %s {\n", method.getMethodName()));

        // declare nodes
        printNonTerminals(method);
        printTokens(method);
        printSymbols(method);
        printVocabulary(method);

        // edges
        tokenEdges(method);
        nonTerminalTokenEdges(method);
        nonTerminalEdges(method);
        tokenSymbols(method);
        tokenVocabulary(method);
        nextLexicalUse(method);
        assignedFromEdges(method);

        builder.append("}");

        return builder.toString();
    }

    private void assignedFromEdges(MethodGraph method) {
        builder.append("\t// assigned from\n");

        for (Token token : method.getTokens()) {
            Optional<NonTerminal> possibleAssignedFrom = token.getAssignedFrom();
            if(possibleAssignedFrom.isPresent()) {
                NonTerminal assignedFrom = possibleAssignedFrom.get();
                builder.append(String.format("\tT%d -> NT%d [color=brown, label=\"assigned from\",style=dotted];\n", token.getId(), assignedFrom.getId()));
            }
        }

        builder.append("\n");
    }

    private void nextLexicalUse(MethodGraph method) {
        builder.append("\t// next lexical uses\n");

        for (Token token : method.getTokens()) {
            Optional<Token> possibleNextLexicalUse = token.getNextLexicalUse();
            if(possibleNextLexicalUse.isPresent()) {
                Token nextLexicalUse = possibleNextLexicalUse.get();
                builder.append(String.format("\tT%d -> T%d [color=orange, label=\"next lexical use\",style=dashed];\n", token.getId(), nextLexicalUse.getId()));
            }
        }

        builder.append("\n");
    }

    private void tokenVocabulary(MethodGraph method) {
        builder.append("\t// tokens and vocabulary\n");

        for (Token token : method.getTokens()) {
            Optional<Set<Vocabulary>> possibleVocabulary = token.getVocabulary();
            if(possibleVocabulary.isPresent()) {
                Set<Vocabulary> vocabulary = possibleVocabulary.get();
                for (Vocabulary word : vocabulary) {
                    builder.append(String.format("\tV%d -> T%d [color=green, label=\"subtoken of\"];\n", word.getId(), token.getId()));
                }
            }
        }

        builder.append("\n");
    }

    private void tokenSymbols(MethodGraph method) {
        builder.append("\t// tokens and symbols\n");

        for (Token token : method.getTokens()) {
            Optional<Symbol> possibleSymbol = token.getSymbol();

            if(possibleSymbol.isPresent()) {
                Symbol symbol = possibleSymbol.get();
                builder.append(String.format("\tT%d -> S%d [color=black, label=\"occurence of\"];\n", token.getId(), symbol.getId()));
            }
        }

        builder.append("\n");
    }

    private void nonTerminalEdges(MethodGraph method) {
        builder.append("\t// non terminal edges\n");
        nonTerminalEdges(method.getRoot());
        builder.append("\n");
    }

    private void nonTerminalTokenEdges(MethodGraph method) {

        builder.append("\t// non terminal -> token edges\n");
        for (NonTerminal node : method.getNonTerminals()) {
            List<Token> tokens = node.getTokens();
            for (Token token : tokens) {
                builder.append(String.format("\tNT%d -> T%d [color=red, label=\"child\"];\n", node.getId(), token.getId()));
            }
        }
        builder.append("\n");
    }

    private void nonTerminalEdges(NonTerminal parent) {


        List<NonTerminal> children = parent.getChildren();
        for (NonTerminal child : children) {
            builder.append(String.format("\tNT%d -> NT%d [color=red, label=\"child\"];\n", parent.getId(), child.getId()));

            nonTerminalEdges(child);
        }
    }

    private void tokenEdges(MethodGraph method) {
        // there has to be at least two nodes to print the edge here
        LinkedList<Token> tokens = method.getTokens();

        if(tokens.size()<2)
            return;

        builder.append("\t// token edges\n");

        for (int i = 1; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            Token previousToken = tokens.get(i - 1);

            builder.append(String.format("\tT%d -> T%d [color=blue, label=\"next token\"];\n", previousToken.getId(), currentToken.getId()));
        }

        builder.append("\n");

    }

    private void printVocabulary(MethodGraph method) {
        builder.append("\t// vocabulary\n");

        for (Vocabulary vocabulary : method.getVocabulary()) {
            builder.append(String.format("\tV%d ",vocabulary.getId()));
            builder.append(String.format("[label=\"V:%s\",color=green];\n", vocabulary.getWord()));
        }

        builder.append("\n");
    }

    private void printSymbols(MethodGraph method) {
        builder.append("\t// symbols\n");

        for (Symbol symbol : method.getSymbols()) {
            builder.append(String.format("\tS%d ",symbol.getId()));
            builder.append(String.format("[label=\"S:%s\",shape=box,color=grey];\n", symbol.getSymbol()));
        }

        builder.append("\n");
    }

    private void printTokens(MethodGraph method) {
        builder.append("\t// tokens\n");

        for (Token token : method.getTokens()) {
            builder.append(String.format("\tT%d ",token.getId()));
            builder.append(String.format("[label=\"T:%s\",shape=box, color=blue];\n", token.getTokenName()));
        }

        builder.append("\n");
    }

    private void printNonTerminals(MethodGraph method) {
        builder.append("\t// non terminals\n");
        for (NonTerminal nonTerminal : method.getNonTerminals()) {
            builder.append(String.format("\tNT%d ",nonTerminal.getId()));
            builder.append(String.format("[label=\"NT:%s\"];\n", nonTerminal.getType()));
        }

        builder.append("\n");
    }
}

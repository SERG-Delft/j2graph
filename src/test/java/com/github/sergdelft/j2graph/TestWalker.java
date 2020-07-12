package com.github.sergdelft.j2graph;

import com.github.sergdelft.j2graph.graph.NonTerminal;
import com.github.sergdelft.j2graph.graph.Symbol;
import com.github.sergdelft.j2graph.graph.Token;
import com.github.sergdelft.j2graph.graph.Vocabulary;
import com.github.sergdelft.j2graph.walker.Walker;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TestWalker implements Walker {
    @Override
    public void className(String className) {

    }

    @Override
    public void method(String methodName, NonTerminal root) {

    }

    @Override
    public void nonTerminal(NonTerminal nonTerminals) {

    }

    @Override
    public void token(Token tokens) {

    }

    @Override
    public void symbol(Symbol symbols) {

    }

    @Override
    public void vocabulary(Vocabulary vocabulary) {

    }

    @Override
    public void nextToken(Token t1, Token t2) {

    }

    @Override
    public void child(NonTerminal t1, Token t2) {

    }

    @Override
    public void child(NonTerminal t1, NonTerminal t2) {

    }

    @Override
    public void occurenceOf(Token t1, Symbol t2) {

    }

    @Override
    public void subtokenOf(Vocabulary t1, Token t2) {

    }

    @Override
    public void returnsTo(NonTerminal t1, Token t2) {

    }

    @Override
    public void nextLexicalUse(Token t1, Token t2) {

    }

    @Override
    public void assignedFrom(Token t1, NonTerminal t2) {

    }

    @Override
    public void end() {

    }

    @Override
    public void endMethod(String methodName, NonTerminal root) {

    }

    public static TestWalker returnsTo(List<Pair<NonTerminal, Token>> pairs) {
        return new TestWalker() {
            @Override
            public void returnsTo(NonTerminal t1, Token t2) {
                pairs.add(Pair.of(t1, t2));
            }
        };
    }


    public static TestWalker nextLexicalUse(List<Pair<Token, Token>> pairs) {
        return new TestWalker() {
            @Override
            public void nextLexicalUse(Token t1, Token t2) {
                pairs.add(Pair.of(t1, t2));
            }
        };
    }

}

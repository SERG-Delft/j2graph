package com.github.sergdelft.j2graph.walker;

import com.github.sergdelft.j2graph.graph.*;

public interface Walker {
    void className(String className);
    void method(String methodName, NonTerminal root);

    void nonTerminal(NonTerminal nonTerminals);
    void token(Token tokens);
    void symbol(Symbol symbols);
    void vocabulary(Vocabulary vocabulary);

    // edges
    void nextToken(Token t1, Token t2);
    void child(NonTerminal t1, Token t2);
    void child(NonTerminal t1, NonTerminal t2);
    void occurrenceOf(Token t1, Symbol t2);
    void subtokenOf(Vocabulary t1, Token t2);
    void returnsTo(NonTerminal t1, Token t2);

    void nextLexicalUse(Token t1, Token t2);
    void assignedFrom(Token t1, NonTerminal t2);

    void end();
    void endMethod(String methodName, NonTerminal root);
}

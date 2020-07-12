package com.github.sergdelft.j2graph.output.dot;

import com.github.sergdelft.j2graph.graph.*;

public interface MethodGraphVisitor {
    void className(String className);
    void method(String methodName, NonTerminal root);

    void nonTerminal(NonTerminal nonTerminals);
    void token(Token tokens);
    void symbol(Symbol symbols);
    void vocabulary(Vocabulary vocabulary);

    // edges
    void edge(Token t1, Token t2);
    void edge(NonTerminal t1, Token t2);
    void edge(NonTerminal t1, NonTerminal t2);
    void edge(Token t1, Symbol t2);
    void edge(Vocabulary t1, Token t2);

    void lexicalUse(Token t1, Token t2);
    void assignedFrom(Token t1, NonTerminal t2);

    void end();
    void endMethod(String methodName, NonTerminal root);
}

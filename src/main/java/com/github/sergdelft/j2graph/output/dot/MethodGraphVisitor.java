package com.github.sergdelft.j2graph.output.dot;

import com.github.sergdelft.j2graph.graph.NonTerminal;
import com.github.sergdelft.j2graph.graph.Symbol;
import com.github.sergdelft.j2graph.graph.Token;
import com.github.sergdelft.j2graph.graph.Vocabulary;

public interface MethodGraphVisitor {
    void methodName(String methodName);
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

}

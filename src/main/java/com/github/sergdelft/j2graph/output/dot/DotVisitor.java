package com.github.sergdelft.j2graph.output.dot;

import com.github.sergdelft.j2graph.graph.NonTerminal;
import com.github.sergdelft.j2graph.graph.Symbol;
import com.github.sergdelft.j2graph.graph.Token;
import com.github.sergdelft.j2graph.graph.Vocabulary;

public class DotVisitor implements MethodGraphVisitor {

    private StringBuilder builder = new StringBuilder();

    @Override
    public void methodName(String methodName) {
        builder.append(String.format("digraph %s {\n", methodName));
    }

    public String asString() {
        return builder.toString();
    }

    @Override
    public void nonTerminal(NonTerminal nonTerminal) {
        builder.append(String.format("\tNT%d ",nonTerminal.getId()));
        builder.append(String.format("[label=\"NT:%s\"];\n", nonTerminal.getType()));
    }

    @Override
    public void token(Token token) {
        builder.append(String.format("\tT%d ",token.getId()));
        builder.append(String.format("[label=\"T:%s\",shape=box, color=blue];\n", token.getTokenName()));
    }

    @Override
    public void symbol(Symbol symbol) {
        builder.append(String.format("\tS%d ",symbol.getId()));
        builder.append(String.format("[label=\"S:%s\",shape=box,color=grey];\n", symbol.getSymbol()));
    }

    @Override
    public void vocabulary(Vocabulary vocabulary) {
        builder.append(String.format("\tV%d ",vocabulary.getId()));
        builder.append(String.format("[label=\"V:%s\",color=green];\n", vocabulary.getWord()));
    }

    @Override
    public void edge(Token t1, Token t2) {
        builder.append(String.format("\tT%d -> T%d [color=blue, label=\"next token\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void edge(NonTerminal t1, Token t2) {
        builder.append(String.format("\tNT%d -> T%d [color=red, label=\"child\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void edge(NonTerminal t1, NonTerminal t2) {
        builder.append(String.format("\tNT%d -> NT%d [color=red, label=\"child\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void edge(Token t1, Symbol t2) {
        builder.append(String.format("\tT%d -> S%d [color=black, label=\"occurence of\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void edge(Vocabulary t1, Token t2) {
        builder.append(String.format("\tV%d -> T%d [color=green, label=\"subtoken of\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void lexicalUse(Token t1, Token t2) {
        builder.append(String.format("\tT%d -> T%d [color=orange, label=\"next lexical use\",style=dashed];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void assignedFrom(Token t1, NonTerminal t2) {
        builder.append(String.format("\tT%d -> NT%d [color=brown, label=\"assigned from\",style=dotted];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void end() {
        builder.append("}");
    }
}

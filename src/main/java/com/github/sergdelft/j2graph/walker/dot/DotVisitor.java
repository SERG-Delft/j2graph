package com.github.sergdelft.j2graph.walker.dot;

import com.github.sergdelft.j2graph.graph.*;
import com.github.sergdelft.j2graph.walker.Walker;

public class DotVisitor implements Walker {

    private StringBuilder builder = new StringBuilder();

    @Override
    public void className(String className) {
        builder.append(String.format("digraph %s {\n", className));
        builder.append(String.format("\tC [label=\"class %s\"]\n", className));
    }

    @Override
    public void method(String methodName, NonTerminal root) {
        builder.append(String.format("\n\t// ------ begin method %s\n\n", methodName));

        builder.append(String.format("\tM%d [label=\"M:%s\"];\n",root.getId(), methodName));
        builder.append(String.format("\tC -> M%d\n", root.getId()));
        builder.append(String.format("\tM%d -> NT%d\n", root.getId(), root.getId()));
    }

    public String asString() {
        return builder.toString();
    }

    @Override
    public void nonTerminal(NonTerminal nonTerminal) {
        builder.append(String.format("\tNT%d ",nonTerminal.getId()));
        builder.append(String.format("[label=\"NT:%s\"];\n", nonTerminal.getName()));
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
    public void nextToken(Token t1, Token t2) {
        builder.append(String.format("\tT%d -> T%d [color=blue, label=\"next token\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void child(NonTerminal t1, Token t2) {
        builder.append(String.format("\tNT%d -> T%d [color=red, label=\"child\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void child(NonTerminal t1, NonTerminal t2) {
        builder.append(String.format("\tNT%d -> NT%d [color=red, label=\"child\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void occurenceOf(Token t1, Symbol t2) {
        builder.append(String.format("\tT%d -> S%d [color=black, label=\"occurence of\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void subtokenOf(Vocabulary t1, Token t2) {
        builder.append(String.format("\tV%d -> T%d [color=green, label=\"subtoken of\"];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void returnsTo(NonTerminal t1, Token t2) {
        builder.append(String.format("\tT%d -> NT%d [color=black, style=dotted, label=\"returns to\"];\n", t2.getId(), t1.getId()));
    }

    @Override
    public void nextLexicalUse(Token t1, Token t2) {
        builder.append(String.format("\tT%d -> T%d [color=orange, label=\"next lexical use\",style=dashed];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void assignedFrom(Token t1, NonTerminal t2) {
        builder.append(String.format("\tT%d -> NT%d [color=brown, label=\"assigned from\",style=dotted];\n", t1.getId(), t2.getId()));
    }

    @Override
    public void end() {
        builder.append(String.format("}"));
    }

    @Override
    public void endMethod(String methodName, NonTerminal root) {
        builder.append(String.format("\t// ------ end method %s\n\n", methodName));
    }
}

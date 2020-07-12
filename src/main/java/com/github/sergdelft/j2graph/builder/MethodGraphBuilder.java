package com.github.sergdelft.j2graph.builder;

import com.github.sergdelft.j2graph.graph.*;

import java.util.*;

public class MethodGraphBuilder {
    private final ClassGraphBuilder classGraph;
    private final String methodName;
    private NonTerminal root;
    private Map<String, Symbol> symbols;
    private LinkedList<Token> tokens;
    private List<NonTerminal> nonTerminals;

    public MethodGraphBuilder(ClassGraphBuilder classGraph, String methodName) {
        this.classGraph = classGraph;
        this.methodName = methodName;

        this.symbols = new HashMap<>();
        this.tokens = new LinkedList<>();
        this.nonTerminals = new ArrayList<>();
    }

    public NonTerminalBuilder root(String type) {
        NonTerminal node = new NonTerminal(type);
        root = node;

        this.nonTerminals.add(node);

        return new NonTerminalBuilder(this, node);
    }

    Symbol symbol(String symbolName) {
        // keep a single node for each symbol, based on its name
        if(!this.symbols.containsKey(symbolName))
            this.symbols.put(symbolName, new Symbol(symbolName));
        return this.symbols.get(symbolName);
    }

    Set<Vocabulary> addVocabulary(String tokenName) {
        return classGraph.addVocabulary(tokenName);
    }

    Token token(String tokenName, boolean nextLexicalUse) {
        // find last time this token appeared in the method.
        Optional<Token> lastToken = Optional.empty();
        if(nextLexicalUse) {
            lastToken = findLastToken(tokenName);
        }

        // create a new token
        Token newToken = new Token(tokenName);
        tokens.addLast(newToken);

        // link new token to 'next lexical use' of the last time it appeared
        if(nextLexicalUse && lastToken.isPresent()) {
            lastToken.get().nextLexicalUse(newToken);
        }

        return newToken;
    }

    private Optional<Token> findLastToken(String tokenName) {
        Iterator<Token> it = tokens.descendingIterator();
        while(it.hasNext()) {
            Token current = it.next();
            if(current.sameAs(tokenName)) {
                return Optional.of(current);
            }
        }

        return Optional.empty();
    }

    public MethodGraph build() {
        return new MethodGraph(methodName,
                root,
                nonTerminals,
                tokens,
                symbols.values(),
                classGraph.getVocabulary());
    }

    public void addNonTerminal(NonTerminal newNode) {
        this.nonTerminals.add(newNode);
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodGraphBuilder that = (MethodGraphBuilder) o;
        return methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName);
    }
}

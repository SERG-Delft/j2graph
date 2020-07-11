package com.github.sergdelft.j2graph.builder;

import com.github.sergdelft.j2graph.graph.*;

import java.util.*;
import java.util.stream.Collectors;

public class MethodGraphBuilder {
    private final String methodName;
    private NonTerminal root;
    private Map<String, Vocabulary> vocabulary;
    private Map<String, Symbol> symbols;
    private LinkedList<Token> tokens;
    private List<NonTerminal> nonTerminals;

    public MethodGraphBuilder(String methodName) {
        this.methodName = methodName;

        this.vocabulary = new HashMap<>();
        this.symbols = new HashMap<>();
        this.tokens = new LinkedList<>();
        this.nonTerminals = new ArrayList<>();
    }

    public NonTerminalBuilder root(String type) {
        NonTerminal node = new NonTerminal(new NonTerminalType(type));
        root = node;

        this.nonTerminals.add(node);

        return new NonTerminalBuilder(this, node);
    }

    Symbol symbol(String symbolName) {
        if(!this.symbols.containsKey(symbolName))
            this.symbols.put(symbolName, new Symbol(symbolName));
        return this.symbols.get(symbolName);
    }

    Set<Vocabulary> addVocabulary(String tokenName) {
        // todo: better split the words
        String[] words = tokenName.split("_");

        Arrays.stream(words)
                .filter(word -> !vocabulary.containsKey(word))
                .forEach(word -> vocabulary.put(word, new Vocabulary(word)));

        return Arrays.stream(words)
                .map(word -> vocabulary.get(word))
                .collect(Collectors.toSet());
    }

    Token token(String tokenName) {
        // find last time this token appeared.
        Optional<Token> lastToken = findLastToken(tokenName);

        // create a new token
        Token newToken = new Token(tokenName);
        tokens.addLast(newToken);

        // link new token to 'next lexical use' of the last time it appeared
        if(lastToken.isPresent()) {
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
                vocabulary.values());
    }

    public void addNonTerminal(NonTerminal newNode) {
        this.nonTerminals.add(newNode);
    }
}

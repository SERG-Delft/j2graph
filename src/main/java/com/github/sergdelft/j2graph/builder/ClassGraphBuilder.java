package com.github.sergdelft.j2graph.builder;

import com.github.sergdelft.j2graph.graph.*;
import com.github.sergdelft.j2graph.util.WordCounter;

import java.util.*;
import java.util.stream.Collectors;

public class ClassGraphBuilder {
    private final String className;
    private final Map<String, Vocabulary> vocabulary;
    private final List<MethodGraph> methods;

    public ClassGraphBuilder(String className) {
        this.className = className;
        this.vocabulary = new HashMap<>();
        this.methods = new ArrayList<>();
    }

    Set<Vocabulary> addVocabulary(String tokenName) {
        // break a word into many words, according to a heuristic
        Collection<String> words = WordCounter.breakString(tokenName);

        // add new words to the large node dictionary
        words.stream()
                .filter(word -> !vocabulary.containsKey(word))
                .forEach(word -> vocabulary.put(word, new Vocabulary(word)));

        // return vocabulary nodes related to the words in the 'tokenName'
        return words.stream()
                .map(word -> vocabulary.get(word))
                .collect(Collectors.toSet());
    }

    Collection<Vocabulary> getVocabulary() {
        return vocabulary.values();
    }

    public ClassGraph build() {
        return new ClassGraph(className, methods);
    }

    public void addMethod(MethodGraph graph) {
        methods.add(graph);
    }
}

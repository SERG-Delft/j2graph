package com.github.sergdelft.j2graph.builder;

import com.github.sergdelft.j2graph.graph.*;
import com.github.sergdelft.j2graph.util.WordCounter;

import java.util.*;
import java.util.stream.Collectors;

public class ClassGraphBuilder {
    private final String className;
    private final Map<String, Set<String>> methodInvocations;
    private final Map<String, Vocabulary> vocabulary;
    private final List<MethodGraphBuilder> methodBuilders;

    public ClassGraphBuilder(String className, Map<String, Set<String>> methodInvocations) {
        this.className = className;
        this.methodInvocations = methodInvocations;
        this.vocabulary = new HashMap<>();
        this.methodBuilders = new ArrayList<>();
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
        List<MethodGraph> methods = new ArrayList<>();

        // we build method by method
        for (MethodGraphBuilder methodBuilder : methodBuilders) {
            MethodGraph methodGraph = methodBuilder.build();
            methods.add(methodGraph);
        }

        // Map the map invocations.
        // link the method 'returns' NonTerminal to the
        // MethodInvocation non terminals of the other methods
        for (MethodGraph method : methods) {
            List<NonTerminalMethodInvocation> invokedMethods = method.methodInvocations();

            for (NonTerminalMethodInvocation invokedMethodNT : invokedMethods) {
                String invokedMethod = invokedMethodNT.getInvokedMethod();

                // if the invoked method is actually part of the class,
                // we then create a returns to edge from the invoked method to the
                // MethodInvocation non terminal node.
                Optional<MethodGraph> invokedMethodGraph = findMethod(methods, invokedMethod);
                if(invokedMethodGraph.isPresent()) {
                    invokedMethodGraph.get().returnsTo(invokedMethodNT);
                }
            }
        }

        return new ClassGraph(className, methods);
    }

    private Optional<MethodGraph> findMethod(List<MethodGraph> methods, String invokedMethod) {
        return methods.stream().filter(m -> m.getMethodName().equals(invokedMethod))
                .findFirst();
    }

    public void addMethod(MethodGraphBuilder graph) {
        methodBuilders.add(graph);
    }
}

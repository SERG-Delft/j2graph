package com.github.sergdelft.j2graph;

import com.github.sergdelft.j2graph.ast.JDT;
import com.github.sergdelft.j2graph.graph.*;
import com.github.sergdelft.j2graph.walker.GraphWalker;
import com.github.sergdelft.j2graph.walker.dot.DotVisitor;
import org.apache.commons.collections4.ListUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestBase {

    protected final DotVisitor dot = new DotVisitor();
    protected final GraphWalker out = new GraphWalker();

    protected String loadFixture(String fixture) {
        try {
            return new String (Files.readAllBytes(Paths.get(fixture)));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ClassGraph run(String javaFile) {
        String sourceCode = loadFixture("fixture/" + javaFile);
        ClassGraph graph = new JDT().parse(sourceCode);

        // print the dot file for debugging
        out.accept(graph, dot);
        System.out.println(dot.asString());

        return graph;
    }

    protected Stream<Token> tokensOf(ClassGraph graph, String methodName) {
        return method(graph, methodName)
                .map(m -> m.getTokens())
                .flatMap(m -> m.stream());
    }

    protected Stream<NonTerminal> nonTerminals(ClassGraph graph, String methodName, String nonTerminal) {
        return method(graph, methodName)
                .map(m -> m.getNonTerminals())
                .flatMap(m -> m.stream())
                .filter(nt -> nt.getName().equals(nonTerminal));
    }

    protected Stream<MethodGraph> method(ClassGraph graph, String methodName) {
        return graph.getMethods().stream()
                .filter(m -> m.getMethodName().equals(methodName));
    }

    protected boolean tokenSequence(List<Token> tokens, String... sequence) {
        List<String> expectedSequence = Arrays.asList(sequence);
        List<String> tokenList = tokens.stream().map(t -> t.getTokenName()).collect(Collectors.toList());

        List<String> lcs = ListUtils.longestCommonSubsequence(tokenList, expectedSequence);

        return lcs.size() == expectedSequence.size();
    }

    protected boolean nonTerminalExists(ClassGraph graph, String methodName, String nonTerminal) {
        return nonTerminals(graph, methodName, nonTerminal).count() == 1;
    }

    protected Stream<Vocabulary> vocabularyOf(ClassGraph graph, String methodName) {
        return method(graph, methodName)
                .map(m -> m.getVocabulary())
                .flatMap(v -> v.stream());
    }


}

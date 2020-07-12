package com.github.sergdelft.j2graph;

import com.github.sergdelft.j2graph.ast.JDT;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.walker.dot.DotVisitor;
import com.github.sergdelft.j2graph.walker.GraphWalker;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class J2GraphTest {

    private final DotVisitor dot = new DotVisitor();
    private final GraphWalker out = new GraphWalker();

    @Test
    void tA() {
        String sourceCode = loadFixture("fixture/A.java");
        ClassGraph graph = new JDT().parse(sourceCode);

        out.accept(graph, dot);
        System.out.println(dot.asString());
    }

    @Test
    void tB() {
        String sourceCode = loadFixture("fixture/B.java");
        ClassGraph graph = new JDT().parse(sourceCode);

        out.accept(graph, dot);
        System.out.println(dot.asString());
    }

    @Test
    void tC() {
        String sourceCode = loadFixture("fixture/C.java");
        ClassGraph graph = new JDT().parse(sourceCode);

        out.accept(graph, dot);
        System.out.println(dot.asString());
    }

    @Test
    void tD() {
        String sourceCode = loadFixture("fixture/D.java");
        ClassGraph graph = new JDT().parse(sourceCode);

        out.accept(graph, dot);
        System.out.println(dot.asString());
    }

    @Test
    void tE() {
        String sourceCode = loadFixture("fixture/E.java");
        ClassGraph graph = new JDT().parse(sourceCode);

        out.accept(graph, dot);
        System.out.println(dot.asString());
    }

    private String loadFixture(String fixture) {
        try {
            return new String (Files.readAllBytes(Paths.get(fixture)));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}

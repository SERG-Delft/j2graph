package com.github.sergdelft.j2graph;

import com.github.sergdelft.j2graph.ast.JDT;
import com.github.sergdelft.j2graph.graph.MethodGraph;
import com.github.sergdelft.j2graph.output.dot.DotVisitor;
import com.github.sergdelft.j2graph.output.dot.OutputGenerator;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JDTTest {

    private final DotVisitor dot = new DotVisitor();
    private final OutputGenerator out = new OutputGenerator();

    @Test
    void t1() {
        String sourceCode = loadFixture("fixture/A.java");
        List<MethodGraph> graphs = new JDT().parse(sourceCode);

        out.accept(graphs.get(0), dot);
        System.out.println(dot.asString());
    }

    @Test
    void t2() {
        String sourceCode = loadFixture("fixture/B.java");
        List<MethodGraph> graphs = new JDT().parse(sourceCode);

        out.accept(graphs.get(0), dot);
        System.out.println(dot.asString());
    }

    @Test
    void t3() {
        String sourceCode = loadFixture("fixture/C.java");
        List<MethodGraph> graphs = new JDT().parse(sourceCode);

        out.accept(graphs.get(0), dot);
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

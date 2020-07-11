package com.github.sergdelft.j2graph;

import com.github.sergdelft.j2graph.ast.JDT;
import com.github.sergdelft.j2graph.graph.MethodGraph;
import com.github.sergdelft.j2graph.output.dot.DotGenerator;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JDTTest {

    final DotGenerator dotGenerator = new DotGenerator();

    @Test
    void t1() {
        String sourceCode = loadFixture("fixture/A.java");
        List<MethodGraph> graphs = new JDT().parse(sourceCode);
        System.out.println(dotGenerator.generate(graphs.get(0)));
    }

    @Test
    void t2() {
        String sourceCode = loadFixture("fixture/B.java");
        List<MethodGraph> graphs = new JDT().parse(sourceCode);
        System.out.println(dotGenerator.generate(graphs.get(0)));
    }

    private String loadFixture(String fixture) {
        try {
            return new String (Files.readAllBytes(Paths.get(fixture)));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}

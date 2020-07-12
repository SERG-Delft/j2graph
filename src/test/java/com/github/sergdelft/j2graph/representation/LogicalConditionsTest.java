package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.graph.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises different booleans operations, e.g.,
 * &&, ||, ...
 */
public class LogicalConditionsTest extends TestBase {

    private ClassGraph graph;

    @BeforeEach
    void run() {
        graph = run("representation/LogicalConditions.java");
    }

    @Test
    void and_conditions() {

        assertThat(nonTerminalExists(graph, "m1/0", "IfStatement"))
                .isTrue();

        // the token sequence exists
        List<Token> tokens = tokensOf(graph, "m1/0")
                .collect(Collectors.toList());

        assertThat(tokenSequence(tokens, "if", "a", ">", "20", "&&", "b", "<", "20"))
                .isTrue();

    }


}

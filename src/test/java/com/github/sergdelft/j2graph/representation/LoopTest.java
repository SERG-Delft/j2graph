package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.graph.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise the representation of
 * loops
 */
public class LoopTest extends TestBase {

    private ClassGraph graph = run("representation/Loop.java");

    @Test
    void for_loops() {
        // there is a for statement non terminal node
        assertThat(nonTerminalExists(graph, "m1/0", "ForStatement"))
                .isTrue();

        // the token sequence exists
        List<Token> tokens = tokensOf(graph, "m1/0")
                .collect(Collectors.toList());

        assertThat(tokenSequence(tokens, "for", "int", "i", "0", "i", "<", "10", "i"))
            .isTrue();

    }

    @Test
    void while_loops() {
        // there is a while statement non terminal node
        assertThat(nonTerminalExists(graph, "m2/0", "WhileStatement"))
                .isTrue();

        // the token sequence exists
        List<Token> tokens = tokensOf(graph, "m2/0")
                .collect(Collectors.toList());

        assertThat(tokenSequence(tokens, "while", "i", ">", "10"))
                .isTrue();

    }
}

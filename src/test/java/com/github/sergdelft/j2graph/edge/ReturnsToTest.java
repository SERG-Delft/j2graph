package com.github.sergdelft.j2graph.edge;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.graph.NonTerminal;
import com.github.sergdelft.j2graph.graph.Token;
import com.github.sergdelft.j2graph.walker.GraphWalker;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.sergdelft.j2graph.TestWalker.returnsTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise the existence of the
 * "returns to" edges
 */
public class ReturnsToTest extends TestBase {

    private ClassGraph graph = run("edge/ReturnsTo.java");

    @Test
    void all_return_tokens_linked_to_method_invocation() {
        List<Token> returnTokens = tokensOf(graph, "m2/0")
                .filter(t -> t.isReturn())
                .collect(Collectors.toList());

        assertThat(returnTokens).hasSize(2);
        assertThat(returnTokens.get(0).getListOfNonTerminalsToReturnTo()).isNotEmpty();
        assertThat(returnTokens.get(1).getListOfNonTerminalsToReturnTo()).isNotEmpty();

        List<Token> returnTokensM1 = tokensOf(graph, "m1/0")
                .filter(t -> t.isReturn())
                .collect(Collectors.toList());

        assertThat(returnTokensM1).hasSize(1);
        assertThat(returnTokensM1.get(0).getListOfNonTerminalsToReturnTo()).isEmpty();
    }

    @Test
    void returns_in_the_walker() {
        final List<Pair<NonTerminal, Token>> pairs = new ArrayList<>();
        new GraphWalker().accept(graph, returnsTo(pairs));
        assertThat(pairs).hasSize(2);
    }



}

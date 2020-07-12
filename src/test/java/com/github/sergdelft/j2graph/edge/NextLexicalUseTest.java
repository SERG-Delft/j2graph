package com.github.sergdelft.j2graph.edge;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.graph.Token;
import com.github.sergdelft.j2graph.walker.GraphWalker;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.sergdelft.j2graph.TestWalker.nextLexicalUse;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise the existence of the
 * "next lexical use" edges
 */
public class NextLexicalUseTest extends TestBase {

    private final ClassGraph graph = run("edge/NextLexicalUse.java");

    @Test
    void walk_through_next_lexical_uses() {
        final List<Pair<Token, Token>> pairs = new ArrayList<>();
        new GraphWalker().accept(graph, nextLexicalUse(pairs));

        assertThat(pairs).hasSize(6);

        List<Pair<Token, Token>> pairsOfA = pairsOfSameTokens(pairs, "a");
        List<Pair<Token, Token>> pairsOfB = pairsOfSameTokens(pairs, "b");

        assertThat(pairsOfA).hasSize(4);
        assertThat(pairsOfB).hasSize(2);

    }

    private List<Pair<Token, Token>> pairsOfSameTokens(List<Pair<Token, Token>> pairs, String tokenName) {
        return pairs
                .stream()
                .filter(p -> p.getLeft().getTokenName().equals(tokenName)
                && p.getRight().getTokenName().equals(tokenName))
                .collect(Collectors.toList());
    }


}

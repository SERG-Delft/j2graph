package com.github.sergdelft.j2graph.node;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class VocabularyTest extends TestBase {

    private ClassGraph graph = run("node/Vocabulary.java");

    @Test
    void gets_all_words_from_code() {

        Set<String> words = vocabularyOf(graph, "m1/0")
                .map(v -> v.getWord())
                .collect(Collectors.toSet());

        assertThat(words)
                .containsExactlyInAnyOrder("m1", "i", "foo", "get", "some", "variable", "named", "like", "this", "another", "here");

    }
}

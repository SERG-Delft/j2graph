package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise the classes with imports.
 * This was a bug (issue #3)
 */
public class ImportTest extends TestBase {

    private ClassGraph graph = run("representation/Imports.java");

    @Test
    void dont_break_with_imports() {
        assertThat(graph.getMethods()).isNotEmpty();
    }

}

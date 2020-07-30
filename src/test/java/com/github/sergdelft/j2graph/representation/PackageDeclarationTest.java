package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise the classes with package declaration.
 * We also observed that a modifier in the 'class declaration' (e.g., public class)
 * was breaking it. So we also fix this here.
 * This was a bug (issue #4)
 */
public class PackageDeclarationTest extends TestBase {

    private ClassGraph graph = run("representation/PackageDeclaration.java");

    @Test
    void dont_break_with_package_declaration() {
        assertThat(graph.getMethods()).isNotEmpty();
    }

}

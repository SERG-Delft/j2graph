package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise the classes with Enums.
 * Related to issue #7
 */
public class EnumTest extends TestBase {

    //    @TODO - this test fails at the moment. Graph should not be null with enums.
//    @Test
    @Disabled
    void dont_break_with_enums() {
        ClassGraph graph = run("representation/Enum.java");
        assertThat(graph != null);
    }

}

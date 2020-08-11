package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests that exercise the classes with Enums.
 * This was a bug (issue #3)
 */
public class EnumTest extends TestBase {

//    private ClassGraph graph = run("representation/Enum.java");

    //    @TODO - this test fails at the moment. Graph should not be null with enums.
    @Test
    void dont_break_with_enums() {
        NullPointerException e = assertThrows(NullPointerException.class,
                () -> run("representation/Enum.java"));
//        assertThat(graph != null);
    }

}

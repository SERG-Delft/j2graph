package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.ast.JDT;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests that exercise the classes with imports.
 * This was a bug (issue #3)
 */
public class UnparsableCodeTest extends TestBase {

    private String sourceCode = loadFixture("fixture/" + "representation/UnparsableCode.java");

    @Test
    void dont_break_with_unparsable_code() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new JDT().parse(sourceCode));
    }

}

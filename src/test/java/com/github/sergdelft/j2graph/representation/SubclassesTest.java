package com.github.sergdelft.j2graph.representation;

import com.github.sergdelft.j2graph.TestBase;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * For now, we ignore subclasses.
 * This is related to #6
 */
public class SubclassesTest extends TestBase {

    private ClassGraph graph = run("representation/SubClasses.java");

    @Test
    void ignore_sub_classes() {
        // there are 3 methods, as the other two are inside the sub class
        assertThat(graph.getMethods().stream().map(x -> x.getMethodName()))
                .containsExactly("clone/1[ByteBuffer]", "allocate/1[int]", "cloningBTreeRowBuilder/0");
    }

}

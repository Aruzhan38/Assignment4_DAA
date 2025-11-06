package org.example.graph;

import org.example.graph.common.SimpleMetrics;
import org.example.graph.topo.TopologicalSort;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSortTest {

    @Test
    public void testSimpleDAG() {
        List<List<Integer>> dag = List.of(
                List.of(1),
                List.of(2),
                List.of(3),
                List.of()
        );

        var metrics = new SimpleMetrics();
        List<Integer> order = TopologicalSort.kahnOrder(4, dag, metrics);

        assertEquals(4, order.size(), "Should include all 4 nodes");
        assertTrue(order.indexOf(0) < order.indexOf(3), "0 should come before 3");
    }

    @Test
    public void testBranchingDAG() {
        List<List<Integer>> dag = List.of(
                List.of(1, 2),
                List.of(3),
                List.of(3),
                List.of()
        );

        var metrics = new SimpleMetrics();
        List<Integer> order = TopologicalSort.kahnOrder(4, dag, metrics);

        assertEquals(4, order.size(), "Topological order should have 4 elements");
        assertTrue(order.indexOf(0) < order.indexOf(3), "Source 0 should precede 3");
    }

    @Test
    public void testDetectCycle() {
        List<List<Integer>> dag = List.of(
                List.of(1),
                List.of(2),
                List.of(0)
        );

        var metrics = new SimpleMetrics();
        assertThrows(IllegalStateException.class, () ->
                        TopologicalSort.kahnOrder(3, dag, metrics),
                "Should throw exception for cyclic graph"
        );
    }
}

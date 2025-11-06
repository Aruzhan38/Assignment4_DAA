package org.example.graph;

import org.example.graph.common.Graph;
import org.example.graph.common.SimpleMetrics;
import org.example.graph.scc.KosarajuSCC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SCCKosarajuTest {

    @Test
    public void testSimpleCycle() {
        Graph g = new Graph();
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "A", 1);

        var metrics = new SimpleMetrics();
        var result = KosarajuSCC.compute(g, metrics);

        assertEquals(1, result.components.size(), "Should detect one SCC (A-B-C cycle)");
    }

    @Test
    public void testDisconnectedGraph() {
        Graph g = new Graph();
        g.addEdge("A", "B", 1);
        g.addEdge("C", "D", 1);

        var metrics = new SimpleMetrics();
        var result = KosarajuSCC.compute(g, metrics);

        assertTrue(result.components.size() >= 2, "Should find at least two SCCs");
    }
}

package org.example.graph;

import org.example.graph.common.Graph;
import org.example.graph.common.SimpleMetrics;
import org.example.graph.dagsp.DagShortestPaths;
import org.example.graph.dagsp.DagLongestPath;
import org.example.graph.topo.TopologicalSort;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DagPathTest {

    @Test
    public void testShortestPath() {
        Graph g = new Graph();
        g.addEdge("A", "B", 2);
        g.addEdge("B", "C", 3);
        g.addEdge("A", "C", 10);

        int n = g.n();
        List<List<Integer>> dagAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> list = new ArrayList<>();
            for (Graph.Edge e : g.out(i)) list.add(e.to);
            dagAdj.add(list);
        }

        var metrics = new SimpleMetrics();
        List<Integer> topo = TopologicalSort.kahnOrder(n, dagAdj, metrics);
        int src = g.id("A");

        var result = DagShortestPaths.run(g, topo, src, metrics);

        int vC = g.id("C");
        assertEquals(5, result.dist[vC], "Shortest path A→B→C should have total weight 5");
    }

    @Test
    public void testLongestPath() {

        Graph g = new Graph();
        g.addEdge("A", "B", 5);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 6);

        int n = g.n();
        List<List<Integer>> dagAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> list = new ArrayList<>();
            for (Graph.Edge e : g.out(i)) list.add(e.to);
            dagAdj.add(list);
        }

        var metrics = new SimpleMetrics();
        List<Integer> topo = TopologicalSort.kahnOrder(n, dagAdj, metrics);

        var result = DagLongestPath.run(g, topo);

        int vC = g.id("C");
        assertTrue(result.best[vC] >= 6, "Longest path A→C should be at least 6");
    }
}

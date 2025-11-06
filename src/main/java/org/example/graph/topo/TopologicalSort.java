package org.example.graph.topo;
import java.util.*;
import org.example.graph.common.Metrics;

public class TopologicalSort {
    public static List<Integer> kahnOrder(int n, List<List<Integer>> dagAdj, Metrics m) {
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : dagAdj.get(u)) indeg[v]++;
        }

        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) {
                q.add(i);
                m.inc("kahn_push", 1);
            }
        }
        List<Integer> order = new ArrayList<>(n);

        while (!q.isEmpty()) {
            int u = q.remove();
            m.inc("kahn_pop", 1);
            order.add(u);

            for (int v : dagAdj.get(u)) {
                if (--indeg[v] == 0) {
                    q.add(v);
                    m.inc("kahn_push", 1);
                }
            }
        }

        if (order.size() != n)
            throw new IllegalStateException("Graph is not a DAG (cycle detected)");

        return order;
    }
}



package org.example.graph.scc;

import org.example.graph.common.Graph;
import org.example.graph.common.Metrics;
import java.util.*;

public class KosarajuSCC {
    public static class Result {
        public final int[] compId;
        public final List<List<Integer>> components;
        public Result(int[] compId, List<List<Integer>> components){
            this.compId = compId;
            this.components = components;
        }
    }
    public static Result compute(Graph g, Metrics m){
        final int n = g.n();
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>(n);


// Pass 1: DFS on original graph to get finish order
        m.timeStart("scc_dfs1");
        for (int u = 0; u < n; u++) if (!visited[u]) dfs1(g, u, visited, finishOrder, m);
        m.timeEnd("scc_dfs1");


// Pass 2: DFS on transposed graph in reverse finish order
        Graph gt = g.transpose();
        Arrays.fill(visited, false);
        int[] compId = new int[n];
        Arrays.fill(compId, -1);
        List<List<Integer>> components = new ArrayList<>();


        m.timeStart("scc_dfs2");
        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int start = finishOrder.get(i);
            if (!visited[start]) {
                List<Integer> comp = new ArrayList<>();
                dfs2(gt, start, visited, comp, m);
                int cid = components.size();
                for (int v : comp) compId[v] = cid;
                components.add(comp);
            }
        }
        m.timeEnd("scc_dfs2");


        return new Result(compId, components);
    }

    // --- helpers ---
    private static void dfs1(Graph g, int u, boolean[] vis, List<Integer> order, Metrics m){
        vis[u] = true; m.inc("scc_dfs_visits", 1);
        for (Graph.Edge e : g.out(u)) {
            m.inc("scc_dfs_edges", 1);
            if (!vis[e.to]) dfs1(g, e.to, vis, order, m);
        }
        order.add(u); // record by finish time
    }


    private static void dfs2(Graph gt, int u, boolean[] vis, List<Integer> comp, Metrics m){
        vis[u] = true; comp.add(u); m.inc("scc_dfs_visits", 1);
        for (Graph.Edge e : gt.out(u)) {
            m.inc("scc_dfs_edges", 1);
            if (!vis[e.to]) dfs2(gt, e.to, vis, comp, m);
        }
    }
}
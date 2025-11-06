package org.example.graph.dagsp;
import org.example.graph.common.Graph;
import org.example.graph.common.Metrics;
import java.util.*;

public class DagShortestPaths {

    public static class Result {
        public final int src;
        public final long[] dist;
        public final int[] parent;

        public Result(int src, long[] dist, int[] parent){
            this.src = src;
            this.dist = dist;
            this.parent = parent;
        }

        public List<Integer> pathTo(int v){
            if (dist[v] == Long.MAX_VALUE) return List.of();
            ArrayDeque<Integer> st = new ArrayDeque<>();
            for (int cur = v; cur != -1; cur = parent[cur]) st.push(cur);
            return new ArrayList<>(st);
        }
    }

    public static Result run(Graph g, List<Integer> topo, int src, Metrics m){
        int n = g.n();
        long[] dist = new long[n]; Arrays.fill(dist, Long.MAX_VALUE); dist[src] = 0L;
        int[] parent = new int[n]; Arrays.fill(parent, -1);

        m.timeStart("dag_sp");
        for (int u : topo) {
            long du = dist[u];
            if (du == Long.MAX_VALUE) continue;
            for (Graph.Edge e : g.out(u)) {
                long nd = du + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    parent[e.to] = u;
                    m.inc("dag_relax", 1);
                }
            }
        }
        m.timeEnd("dag_sp");
        return new Result(src, dist, parent);
    }
}
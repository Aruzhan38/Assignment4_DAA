package org.example.graph.dagsp;
import org.example.graph.common.Graph;
import java.util.*;

public class DagLongestPath {

    public static class Result {
        public final long[] best;
        public final int[] parent;
        public final int end;

        public Result(long[] best, int[] parent, int end){
            this.best = best;
            this.parent = parent;
            this.end = end;
        }

        public List<Integer> path(){
            ArrayDeque<Integer> st = new ArrayDeque<>();
            boolean[] seen = new boolean[parent.length];
            int cur = end, steps = 0, limit = parent.length;

            while (cur != -1 && !seen[cur] && steps <= limit) {
                seen[cur] = true;
                st.push(cur);
                cur = parent[cur];
                steps++;
            }

            return new ArrayList<>(st);
        }
    }

    public static Result run(Graph g, List<Integer> topo){
        int n = g.n();
        long NEG_INF = Long.MIN_VALUE / 4;
        long[] best = new long[n]; Arrays.fill(best, NEG_INF);
        int[] parent = new int[n]; Arrays.fill(parent, -1);

        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (Graph.Edge e : g.out(u)) indeg[e.to]++;
        for (int i = 0; i < n; i++) if (indeg[i] == 0) best[i] = 0;

        int[] pos = new int[n];
        for (int i = 0; i < topo.size(); i++) pos[topo.get(i)] = i;

        for (int u : topo) {
            if (best[u] == NEG_INF) continue;
            for (Graph.Edge e : g.out(u)) {
                if (pos[e.to] <= pos[u]) continue;

                long cand = best[u] + e.w;
                if (cand > best[e.to]) {
                    best[e.to] = cand;
                    parent[e.to] = u;
                }
            }
        }
        int end = 0; for (int i = 1; i < n; i++) if (best[i] > best[end]) end = i;
        return new Result(best, parent, end);
    }

}

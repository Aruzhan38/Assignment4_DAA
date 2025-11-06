package org.example.graph.scc;

import org.example.graph.common.Graph;
import java.util.*;


public class Condensation {
    public static class Result {
        public final List<List<Integer>> dagAdj;
        public final boolean[][] hasEdge;
        public Result(List<List<Integer>> dagAdj, boolean[][] hasEdge){
            this.dagAdj = dagAdj; this.hasEdge = hasEdge;
        }
    }

    public static Result build(Graph g, KosarajuSCC.Result scc){
        int compCount = scc.components.size();
        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < compCount; i++) dag.add(new ArrayList<>());
        boolean[][] seen = new boolean[compCount][compCount];


// for each original edge u->v, add C(u)->C(v) if components differ
        for (int u = 0; u < g.n(); u++) {
            int cu = scc.compId[u];
            for (Graph.Edge e : g.out(u)) {
                int cv = scc.compId[e.to];
                if (cu != cv && !seen[cu][cv]) { dag.get(cu).add(cv); seen[cu][cv] = true; }
            }
        }
        return new Result(dag, seen);
    }
}

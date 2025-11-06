package org.example;
import org.example.graph.common.*;
import org.example.graph.scc.*;
import org.example.graph.topo.*;
import org.example.graph.dagsp.*;
import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        String filePath = args.length > 0 ? args[0] : "data/sample-small.json";
        String srcName = null;
        for (String a : args) if (a.startsWith("SOURCE=")) srcName = a.substring(7);

        Graph g = Graphs.loadJson(new File(filePath));
        SimpleMetrics metrics = new SimpleMetrics();

        System.out.println("Loaded graph with " + g.n() + " nodes.");

        var scc = KosarajuSCC.compute(g, metrics);
        System.out.println("\nSCC components:");
        for (int cid = 0; cid < scc.components.size(); cid++) {
            List<Integer> comp = scc.components.get(cid);
            System.out.print("  C" + cid + " (size=" + comp.size() + "): ");
            for (int i = 0; i < comp.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(g.name(comp.get(i)));
            }
            System.out.println();
        }

        var cond = Condensation.build(g, scc);
        System.out.println("\nCondensation DAG edges:");
        for (int u = 0; u < cond.dagAdj.size(); u++) {
            for (int v : cond.dagAdj.get(u)) {
                System.out.println("  C" + u + " -> C" + v);
            }
        }

        var topoOrder = TopologicalSort.kahnOrder(cond.dagAdj.size(), cond.dagAdj, metrics);
        System.out.println("\nTopological order of components: " + topoOrder);

        List<Integer> topoNodes = new ArrayList<>();
        for (int cid : topoOrder) topoNodes.addAll(scc.components.get(cid));
        System.out.println("Derived node order: ");
        for (int i = 0; i < topoNodes.size(); i++) {
            if (i > 0) System.out.print(" -> ");
            System.out.print(g.name(topoNodes.get(i)));
        }
        System.out.println();

        int src = (srcName != null && g.id(srcName) != null) ? g.id(srcName) : topoNodes.get(0);
        var sp = DagShortestPaths.run(g, topoNodes, src, metrics);
        System.out.println("\nShortest distances from SOURCE=" + g.name(src));
        for (int v : topoNodes) {
            String d = sp.dist[v] == Long.MAX_VALUE ? "∞" : Long.toString(sp.dist[v]);
            System.out.printf("  %s : %s\n", g.name(v), d);
        }

        int target = topoNodes.get(topoNodes.size() - 1);
        List<Integer> path = sp.pathTo(target);
        if (!path.isEmpty()) {
            System.out.print("Example shortest path to " + g.name(target) + ": ");
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) System.out.print(" -> ");
                System.out.print(g.name(path.get(i)));
            }
            System.out.println();
        }

        var lp = DagLongestPath.run(g, topoNodes);
        System.out.println("\nCritical (longest) path length: " + lp.best[lp.end]);
        System.out.print("Critical path: ");
        List<Integer> cpath = lp.path();
        for (int i = 0; i < cpath.size(); i++) {
            if (i > 0) System.out.print(" -> ");
            System.out.print(g.name(cpath.get(i)));
        }
        System.out.println();

        System.out.println(metrics.pretty());
    }
}
package org.example.graph.common;
import java.util.*;


public class Graph {
    public static class Edge {
        public final int to;
        public final int w;

        public Edge(int to, int w){
            this.to=to;
            this.w=w;
        }
    }

    private final List<String> idToName = new ArrayList<>();
    private final Map<String,Integer> nameToId = new HashMap<>();
    private final List<List<Edge>> adj = new ArrayList<>();

    public int addNode(String name){
        return nameToId.computeIfAbsent(name, n -> { int id=idToName.size(); idToName.add(n); adj.add(new ArrayList<>()); return id; });
    }
    public void addEdge(String from, String to, int w){ int u=addNode(from), v=addNode(to); adj.get(u).add(new Edge(v, w)); }


    public int n(){
        return idToName.size();
    }
    public List<Edge> out(int u){
        return adj.get(u);
    }
    public String name(int id){
        return idToName.get(id);
    }
    public Integer id(String name){
        return nameToId.get(name);
    }
    
    public Graph transpose(){
        Graph g = new Graph();
        for (String s : idToName) g.addNode(s);
        for (int u=0; u<n(); u++) for (Edge e: adj.get(u)) g.adj.get(e.to).add(new Edge(u, e.w));
        return g;
    }
}